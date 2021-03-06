/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2013 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */
package org.jahia.unomi.services;

import org.jahia.unomi.lists.UserList;
import org.oasis_open.contextserver.api.Metadata;
import org.oasis_open.contextserver.api.PartialList;
import org.oasis_open.contextserver.api.Profile;
import org.oasis_open.contextserver.api.campaigns.Campaign;
import org.oasis_open.contextserver.api.campaigns.CampaignDetail;
import org.oasis_open.contextserver.api.conditions.Condition;
import org.oasis_open.contextserver.api.query.Query;
import org.oasis_open.contextserver.api.services.DefinitionsService;
import org.oasis_open.contextserver.persistence.spi.PersistenceService;

import java.util.*;

/**
 * @author Christophe Laprun
 */
public class UserListServiceImpl implements UserListService {
    private PersistenceService persistenceService;

    private DefinitionsService definitionsService;

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setDefinitionsService(DefinitionsService definitionsService) {
        this.definitionsService = definitionsService;
    }

    public PartialList<Metadata> getListMetadatas(int offset, int size, String sortBy) {
        PartialList<UserList> userLists = persistenceService.getAllItems(UserList.class, offset, size, sortBy);
        List<Metadata> metadata = new LinkedList<>();
        for (UserList definition : userLists.getList()) {
            metadata.add(definition.getMetadata());
        }
        return new PartialList<>(metadata, userLists.getOffset(), userLists.getPageSize(), userLists.getTotalSize());
    }

    public PartialList<Metadata> getListMetadatas(Query query) {
        if(query.isForceRefresh()){
            persistenceService.refresh();
        }
        definitionsService.resolveConditionType(query.getCondition());
        PartialList<UserList> userLists = persistenceService.query(query.getCondition(), query.getSortby(), UserList.class, query.getOffset(), query.getLimit());
        List<Metadata> metadata = new LinkedList<>();
        for (UserList definition : userLists.getList()) {
            metadata.add(definition.getMetadata());
        }
        return new PartialList<>(metadata, userLists.getOffset(), userLists.getPageSize(), userLists.getTotalSize());
    }

    @Override
    public UserList load(String listId) {
        return persistenceService.load(listId, UserList.class);
    }

    @Override
    public void save(UserList list) {
        persistenceService.save(list);
    }

    @Override
    public void delete(String listId) {
        Condition query = new Condition(definitionsService.getConditionType("profilePropertyCondition"));
        query.setParameter("propertyName", "systemProperties.lists");
        query.setParameter("comparisonOperator", "equals");
        query.setParameter("propertyValue", listId);

        List<Profile> profiles = persistenceService.query(query, null, Profile.class);
        Map<String, Object> profileProps;
        for (Profile p : profiles) {
            profileProps = p.getSystemProperties();
            if(profileProps != null && profileProps.get("lists") != null) {
                int index = ((List) profileProps.get("lists")).indexOf(listId);
                if(index != -1){
                    ((List) profileProps.get("lists")).remove(index);
                    persistenceService.update(p.getItemId(), null, Profile.class, "systemProperties", profileProps);
                }
            }
        }

        persistenceService.remove(listId, UserList.class);
    }
}
