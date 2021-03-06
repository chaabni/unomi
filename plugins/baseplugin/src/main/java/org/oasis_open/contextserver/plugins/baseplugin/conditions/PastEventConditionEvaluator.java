package org.oasis_open.contextserver.plugins.baseplugin.conditions;

/*
 * #%L
 * Context Server Plugin - Provides conditions based on past events
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Jahia Solutions
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.oasis_open.contextserver.api.Event;
import org.oasis_open.contextserver.api.Item;
import org.oasis_open.contextserver.api.Profile;
import org.oasis_open.contextserver.api.conditions.Condition;
import org.oasis_open.contextserver.api.services.DefinitionsService;
import org.oasis_open.contextserver.persistence.elasticsearch.conditions.ConditionEvaluator;
import org.oasis_open.contextserver.persistence.elasticsearch.conditions.ConditionEvaluatorDispatcher;
import org.oasis_open.contextserver.persistence.spi.PersistenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PastEventConditionEvaluator implements ConditionEvaluator {

    private PersistenceService persistenceService;

    private DefinitionsService definitionsService;

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setDefinitionsService(DefinitionsService definitionsService) {
        this.definitionsService = definitionsService;
    }

    @Override
    public boolean eval(Condition condition, Item item, Map<String, Object> context, ConditionEvaluatorDispatcher dispatcher) {

        final Map<String, Object> parameters = condition.getParameterValues();

        Condition eventCondition = (Condition) parameters.get("eventCondition");

        long count;

        if (parameters.containsKey("generatedPropertyKey")) {
            String key = (String) parameters.get("generatedPropertyKey");
            Profile profile = (Profile) item;
            Map<String,Object> pastEvents = (Map<String, Object>) profile.getSystemProperties().get("pastEvents");
            if (pastEvents != null) {
                Number l = (Number) pastEvents.get(key);
                count = l != null ? l.longValue() : 0L;
            } else {
                count = 0;
            }

        } else {
            if (eventCondition == null) {
                throw new IllegalArgumentException("No eventCondition");
            }

            List<Condition> l = new ArrayList<Condition>();
            Condition andCondition = new Condition();
            andCondition.setConditionType(definitionsService.getConditionType("booleanCondition"));
            andCondition.setParameter("operator", "and");
            andCondition.setParameter("subConditions", l);

            l.add(eventCondition);

            Condition profileCondition = new Condition();
            profileCondition.setConditionType(definitionsService.getConditionType("sessionPropertyCondition"));
            profileCondition.setParameter("propertyName", "profileId");
            profileCondition.setParameter("comparisonOperator", "equals");
            profileCondition.setParameter("propertyValue", item.getItemId());
            l.add(profileCondition);

            Integer numberOfDays = (Integer) condition.getParameter("numberOfDays");
            if (numberOfDays != null) {
                Condition numberOfDaysCondition = new Condition();
                numberOfDaysCondition.setConditionType(definitionsService.getConditionType("sessionPropertyCondition"));
                numberOfDaysCondition.setParameter("propertyName", "timeStamp");
                numberOfDaysCondition.setParameter("comparisonOperator", "greaterThan");
                numberOfDaysCondition.setParameter("propertyValueDateExpr", "now-" + numberOfDays + "d");
                l.add(numberOfDaysCondition);
            }
            count = persistenceService.queryCount(andCondition, Event.ITEM_TYPE);
        }

        Integer minimumEventCount = !parameters.containsKey("minimumEventCount")  ? 0 : (Integer) parameters.get("minimumEventCount");
        Integer maximumEventCount = !parameters.containsKey("maximumEventCount")  ? Integer.MAX_VALUE : (Integer) parameters.get("maximumEventCount");

        return (minimumEventCount == 0 && maximumEventCount == Integer.MAX_VALUE && count > 0) ||
                (count > minimumEventCount && count < maximumEventCount);
    }
}
