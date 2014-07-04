/**
 * Copyright (c) 2014 SUSE
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.manager.content.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.redhat.rhn.domain.channel.Channel;
import com.redhat.rhn.domain.channel.ChannelFactory;
import com.redhat.rhn.domain.channel.ChannelFamily;
import com.redhat.rhn.domain.channel.ChannelFamilyFactory;
import com.redhat.rhn.domain.channel.ContentSource;
import com.redhat.rhn.domain.channel.test.ChannelFactoryTest;
import com.redhat.rhn.domain.channel.test.ChannelFamilyFactoryTest;
import com.redhat.rhn.domain.product.SUSEProduct;
import com.redhat.rhn.domain.product.SUSEProductFactory;
import com.redhat.rhn.domain.rhnpackage.PackageFactory;
import com.redhat.rhn.manager.content.ContentSyncManager;
import com.redhat.rhn.testing.RhnBaseTestCase;
import com.redhat.rhn.testing.TestUtils;
import com.suse.scc.model.SCCProduct;

/**
 * Tests for {@link ContentSyncManager}.
 */
public class ContentSyncManagerTest extends RhnBaseTestCase {

    /**
     * Test for {@link ContentSyncManager#updateChannels()}.
     * @throws Exception
     */
    public void testUpdateChannels() throws Exception {
        // Create a test channel and set a specific label
        String channelLabel = "sles11-sp3-pool-x86_64";
        Channel c = createTestVendorChannel();
        c.setLabel(channelLabel);
        c.setDescription("UPDATE ME!");
        c.setName("UPDATE ME!");
        c.setSummary("UPDATE ME!");
        c.setUpdateTag("UPDATE ME!");

        // Setup content source
        ContentSource cs = new ContentSource();
        cs.setLabel(c.getLabel());
        cs.setSourceUrl("UPDATE ME!");
        cs.setType(ChannelFactory.CONTENT_SOURCE_TYPE_YUM);
        cs.setOrg(null);
        cs = (ContentSource) TestUtils.saveAndReload(cs);
        c.getSources().add(cs);
        TestUtils.saveAndFlush(c);

        // Update the channel information
        ContentSyncManager csm = new ContentSyncManager();
        csm.setPathPrefix(getPathPrefix("channels.xml"));
        csm.updateChannels();

        // Verify channel attributes
        c = ChannelFactory.lookupByLabel(channelLabel);
        assertEquals("SUSE Linux Enterprise Server 11 SP3", c.getDescription());
        assertEquals("SLES11-SP3-Pool for x86_64", c.getName());
        assertEquals("SUSE Linux Enterprise Server 11 SP3", c.getSummary());
        assertEquals("slessp3", c.getUpdateTag());

        // Verify content sources (there is only one)
        Set<ContentSource> sources = c.getSources();
        for (ContentSource s : sources) {
            assertEquals("https://nu.novell.com/repo/$RCE/SLES11-SP3-Pool/sle-11-x86_64/",
                    s.getSourceUrl());
        }
    }

    /**
     * Test for {@link ContentSyncManager#updateSUSEProducts()} inserting a new product.
     * @throws Exception
     */
    public void testUpdateSUSEProducts() throws Exception {
        // Create test product attributes
        int productId = 12345;
        assertNull(SUSEProductFactory.lookupByProductId(productId));
        String name = TestUtils.randomString();
        String version = TestUtils.randomString();
        String releaseType = TestUtils.randomString();
        String friendlyName = TestUtils.randomString();
        String productClass = TestUtils.randomString();

        // Setup a product as it comes from SCC
        SCCProduct p = new SCCProduct();
        p.setId(productId);
        p.setName(name);
        p.setVersion(version);
        p.setReleaseType(releaseType);
        p.setFriendlyName(friendlyName);
        p.setProductClass(productClass);
        p.setArch("i686");
        List<SCCProduct> products = new ArrayList<SCCProduct>();
        products.add(p);

        // Call updateSUSEProducts()
        ContentSyncManager csm = new ContentSyncManager();
        csm.updateSUSEProducts(products);

        // Verify that a new product has been created correctly
        SUSEProduct suseProduct = SUSEProductFactory.lookupByProductId(productId);
        assertEquals(name, suseProduct.getName());
        assertEquals(version, suseProduct.getVersion());
        assertEquals(releaseType, suseProduct.getRelease());
        assertEquals(friendlyName, suseProduct.getFriendlyName());
        assertEquals(PackageFactory.lookupPackageArchByLabel("i686"),
                suseProduct.getArch());

        // Verify that a new channel family has been created correctly
        ChannelFamily cf = ChannelFamilyFactory.lookupByLabel(productClass, null);
        assertNotNull(cf);
        assertEquals(cf.getId().toString(), suseProduct.getChannelFamilyId());
    }

    /**
     * Create a vendor channel (org is null) for testing.
     * @return vendor channel for testing
     * @throws Exception (FIXME)
     */
    public static Channel createTestVendorChannel() throws Exception {
        Channel c =  ChannelFactoryTest.createTestChannel(null,
                ChannelFamilyFactoryTest.createTestChannelFamily());
        ChannelFactory.save(c);
        return c;
    }

    /**
     * Finds a given testfile and returns the path prefix.
     * @param filename name of the testfile
     * @return path prefix of testifile
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private String getPathPrefix(String filename) 
            throws ClassNotFoundException, IOException {
        File file = new File(TestUtils.findTestData(filename).getPath());
        return file.getParent() + File.separator;
    }
}
