/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.sreport.api.exceptions;

import digital.srp.sreport.model.Q;

public class ObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7402437013533929364L;

    public ObjectNotFoundException(Class<?> type, Long id) {
        super(String.format("'%1$s' with identifier '%2$d' not found", type.getSimpleName(), id));
    }

    public ObjectNotFoundException(Class<?> type, String id) {
        super(String.format("'%1$s' with identifier '%2$s' not found", type.getSimpleName(), id));
    }

    public ObjectNotFoundException(Class<Q> type, Object id) {
        super(String.format("'%1$s' with identifier '%2$s' not found", type.getSimpleName(), id));
    }

}
