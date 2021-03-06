package org.oasis_open.contextserver.api.query;

/*
 * #%L
 * context-server-api
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggregate {
    private String type;
    private String property;
    private Map<String, Object> parameters = new HashMap<>();
    private List<NumericRange> numericRanges = new ArrayList<>();
    private List<GenericRange> genericRanges = new ArrayList<>();

    public Aggregate() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public List<NumericRange> getNumericRanges() {
        return numericRanges;
    }

    public void setNumericRanges(List<NumericRange> numericRanges) {
        this.numericRanges = numericRanges;
    }

    public List<GenericRange> getGenericRanges() {
        return genericRanges;
    }

    public void setGenericRanges(List<GenericRange> genericRanges) {
        this.genericRanges = genericRanges;
    }
}
