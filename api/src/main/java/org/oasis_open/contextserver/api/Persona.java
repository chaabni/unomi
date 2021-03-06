package org.oasis_open.contextserver.api;

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

/**
 * A persona is a "virtual" profile used to represent categories of profiles, and may also be used to test
 * how a personalized experience would look like using this virtual profile.
 */
public class Persona extends Profile {

    private static final long serialVersionUID = -1239061113528609426L;
    public static final String ITEM_TYPE = "persona";

    public Persona() {
    }

    public Persona(String personaId) {
        super(personaId);
    }

}
