/**
 * Copyright (c) 2018 SUSE LLC
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
package com.suse.manager.virtualization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.suse.manager.webui.services.iface.SystemQuery;

import java.util.Map;
import java.util.Optional;

/**
 * Service providing utility functions to handle virtual machines.
 */
public class VirtManager {

    private final SystemQuery systemQuery;

    /**
     * Service providing utility functions to handle virtual machines.
     * @param systemQueryIn instance for getting information from a system.
     */
    public VirtManager(SystemQuery systemQueryIn) {
        this.systemQuery = systemQueryIn;
    }

    /**
     * Query virtual machine definition
     *
     * @param minionId the host minion ID
     * @param domainName the domain name to look for
     * @return the XML definition or an empty Optional
     */
    public Optional<GuestDefinition> getGuestDefinition(String minionId, String domainName) {
        return systemQuery.getGuestDefinition(minionId, domainName);
    }

    /**
     * Query virtual host and domains capabilities.
     *
     * @param minionId the salt minion virtual host to ask about
     * @return the output of the salt virt.all_capabilities call in JSON
     */
    public Optional<Map<String, JsonElement>> getCapabilities(String minionId) {
        return systemQuery.getCapabilities(minionId);
    }

    /**
     * Query virtual storage pool capabilities
     *
     * @param minionId the salt minion virtual host to ask about
     * @return the output of the salt virt.pool_capabilities call
     */
    public Optional<PoolCapabilitiesJson> getPoolCapabilities(String minionId) {
        return systemQuery.getPoolCapabilities(minionId);
    }

    /**
     * Query virtual storage pool definition
     *
     * @param minionId the host minion ID
     * @param poolName the domain name to look for
     * @return the XML definition or an empty Optional
     */
    public Optional<PoolDefinition> getPoolDefinition(String minionId, String poolName) {
        return systemQuery.getPoolDefinition(minionId, poolName);
    }

    /**
     * Query the list of virtual networks defined on a salt minion.
     *
     * @param minionId the minion to ask about
     * @return a list of the network names
     */
    public Map<String, JsonObject> getNetworks(String minionId) {
        return systemQuery.getNetworks(minionId);
    }

    /**
     * Query the list of virtual storage pools defined on a salt minion.
     *
     * @param minionId the minion to ask about
     * @return a map associating pool names with their informations as Json elements
     */
    public Map<String, JsonObject> getPools(String minionId) {
        return systemQuery.getPools(minionId);
    }

    /**
     * Query the list of virtual storage volumes defined on a salt minion.
     *
     * @param minionId the minion to ask about
     * @return a map associating pool names with the list of volumes it contains mapped by their names
     */
    public Map<String, Map<String, JsonObject>> getVolumes(String minionId) {
        return systemQuery.getVolumes(minionId);
    }

}
