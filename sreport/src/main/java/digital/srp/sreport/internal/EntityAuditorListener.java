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
package digital.srp.sreport.internal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.AuditorAware;

public class EntityAuditorListener {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntityAuditorListener.class);

    /**
     * Sets auditor as the target bean is created if it has a method setCreatedBy.
     *
     * @param target
     */
    @PrePersist
    public void touchForCreate(Object target) {
        if (target instanceof AuditorAware) {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(target.getClass(), "createdBy");
            try {
                pd.getWriteMethod().invoke(target, ((AuditorAware<?>) target).getCurrentAuditor())
                ;
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                LOGGER.error("Unable to set auditor on {}", target, e);
            }
        }
    }

    /**
     * Sets auditor as the target bean is updated if it has a method setUpdatedBy.
     *
     * @param target
     */
    @PreUpdate
    public void touchForUpdate(Object target) {
        if (target instanceof AuditorAware) {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(target.getClass(), "updatedBy");
            try {
                pd.getWriteMethod().invoke(target, ((AuditorAware<?>)target).getCurrentAuditor())
                ;
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                LOGGER.error("Unable to set auditor on {}", target, e);
            }
        }
    }
}
