
package com.edu.bupt.new_account.service;

import com.edu.bupt.new_account.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RuleService {
    List<Rule> getBindedRules(String gateId);

    List<Rule2FilterKey> getBindedR2F(Integer ruleId);

    Filter getBindedFilter(Integer filterId);

    List<Rule2TransFormKey> getBindedR2T(Integer ruleId);

    Transform getBindedTransform(Integer transformId);

    void unbindR2F(Rule2FilterKey R2F);

    void unbindFilter(Integer filterId);

    void unbindR2T(Rule2TransFormKey R2T);

    void unbindTransform(Integer transformId);

    void unbindRule(Integer ruleId);

}
