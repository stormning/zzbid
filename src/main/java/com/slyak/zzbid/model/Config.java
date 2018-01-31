package com.slyak.zzbid.model;

import com.slyak.zzbid.util.Constants;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * .
 *
 * @author stormning 2017/12/20
 * @since 1.3.0
 */
@Entity
@Table(name = "t_config")
@Data
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "cfg")
public class Config {
    @Id
    private Long id = Constants.CONFIG_ID;
    private String name;
    private String password;
    private int interval = 1;
    @Digits(integer = 5, fraction = 2)
    private BigDecimal money = new BigDecimal(1);
    private int retry = 2;
    @Column(name = "start0")
    private boolean start = true;

    @Transient
    public boolean isValid() {
        return name != null && password != null && interval >= 1 && money != null;
    }
}
