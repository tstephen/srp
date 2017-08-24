package digital.srp.sreport.internal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

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
