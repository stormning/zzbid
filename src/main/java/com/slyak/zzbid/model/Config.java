package com.slyak.zzbid.model;

import com.slyak.zzbid.util.Constants;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Calendar;

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
    private static final String CONDITION_PATTERN = "(([0-1]\\d|2[0-3]):[0-5]\\d)-(([0-1]\\d|2[0-3]):[0-5]\\d)";

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
    @Column(name = "_condition")
    private String condition = "08:50-23:20";

    @Transient
    @SneakyThrows
    public boolean canStart() {
        //setup and started
        if (isSetup() && isStart()) {
            //condition not set, or condition pattern is wrong , default start
            if (condition == null || !StringUtils.trimToEmpty(condition).matches(CONDITION_PATTERN)) {
                return true;
            } else {
                String[] split = StringUtils.split(condition, '-');
                int begin = measureStringDate(split[0]);
                int end = measureStringDate(split[1]);
                int now = measureNow();
                //condition check
                return now >= begin && now <= end;
            }
        } else {
            return false;
        }
    }

    @Transient
    public boolean isSetup() {
        return StringUtils.trimToNull(name) != null || StringUtils.trimToNull(password) != null;
    }

    @Transient
    public boolean isValid() {
        return name != null && password != null && interval >= 1 && money != null;
    }

    private static int measureStringDate(String date) {
        return Integer.parseInt(StringUtils.replaceOnce(date, ":", ""));
    }

    private static int measureNow() {
        Calendar instance = Calendar.getInstance();
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int min = instance.get(Calendar.MINUTE);
        return hour * 100 + min;
    }
}
