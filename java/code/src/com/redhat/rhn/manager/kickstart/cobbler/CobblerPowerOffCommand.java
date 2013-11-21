/**
 * Copyright (c) 2013 SUSE
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

package com.redhat.rhn.manager.kickstart.cobbler;

import com.redhat.rhn.common.localization.LocalizationService;
import com.redhat.rhn.common.validator.ValidatorError;
import com.redhat.rhn.domain.server.Server;
import com.redhat.rhn.domain.server.ServerHistoryEvent;
import com.redhat.rhn.domain.user.User;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.cobbler.CobblerConnection;
import org.cobbler.SystemRecord;

import java.util.Date;

/**
 * Powers off a system.
 * @version $Rev$
 */
public class CobblerPowerOffCommand extends CobblerCommand {

    /** The log. */
    private static Logger log = Logger.getLogger(CobblerPowerOffCommand.class);

    /** The server to power off. */
    private Server server;

    /**
     * Instantiates a new Cobbler "power off" command.
     * @param userIn the user running this command
     * @param serverIn the server to power off
     */
    public CobblerPowerOffCommand(User userIn, Server serverIn) {
        super(userIn);
        server = serverIn;
    }

    /**
     * Attempts to power off the server.
     * @return any errors
     */
    @Override
    public ValidatorError store() {
        CobblerConnection connection = getCobblerConnection();

        if (server != null) {
            String cobblerId = server.getCobblerId();
            if (!StringUtils.isEmpty(cobblerId)) {
                SystemRecord systemRecord = SystemRecord.lookupById(connection, cobblerId);
                if (systemRecord != null && systemRecord.getPowerType() != null) {
                    if (systemRecord.powerOff()) {
                        log.debug("Powering off " + server.getId() + " succeded");
                        ServerHistoryEvent event = new ServerHistoryEvent();
                        event.setCreated(new Date());
                        event.setServer(server);
                        event.setSummary("power off");
                        String details = "System has been powered off via " +
                            LocalizationService.getInstance().getPlainText(
                                "kickstart.powermanagement." + systemRecord.getPowerType());
                        event.setDetails(details);
                        server.getHistory().add(event);

                        return null;
                    }
                    else {
                        log.error("Powering off " + server.getId() + " failed");
                        return new ValidatorError(
                            "cobbler.power_management.command_failed");
                    }
                }
            }
        }
        return new ValidatorError("kickstart.powermanagement.not_configured");
    }
}
