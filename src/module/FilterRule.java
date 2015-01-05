/*
 * Copyright 2014-2015 Wesley Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package module;

import com.intellij.ide.util.PropertiesComponent;
import data.SerializeUtil;
import data.StorageDataKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley Lin on 12/14/14.
 */
public class FilterRule {

    private FilterRuleType filterRuleType;
    private String filterString;

    public FilterRule(FilterRuleType filterRuleType, String filterString) {
        this.filterRuleType = filterRuleType;
        this.filterString = filterString;
    }

    public FilterRuleType getFilterRuleType() {
        return filterRuleType;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterRuleType(FilterRuleType filterRuleType) {
        this.filterRuleType = filterRuleType;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public static FilterRule DefaultFilterRule = new FilterRule(FilterRuleType.START_WITH, "NAL_");

    public String toString() {
        return getFilterRuleType().toString() + " <" + getFilterString() + ">";
    }

    public static List<FilterRule> getFilterRulesFromLocal() {
        List<FilterRule> result = new ArrayList<FilterRule>();

        String rules = PropertiesComponent.getInstance().getValue(StorageDataKey.SettingFilterRules);
        if (rules != null) {
            List<FilterRule> ruleList = SerializeUtil.deserializeFilterRuleList(rules);
            result.addAll(ruleList);
        } else {
            result.add(DefaultFilterRule);
        }
        return result;
    }

    public static boolean inFilterRule(String key, List<FilterRule> rules) {
        for (FilterRule rule : rules) {
            switch (rule.getFilterRuleType()) {
                case START_WITH:
                    if (key.startsWith(rule.getFilterString())) {
                        return true;
                    }
                    break;
                case EQUALS:
                    if (key.equals(rule.getFilterString())) {
                        return true;
                    }
                    break;
                case END_WITH:
                    if (key.endsWith(rule.getFilterString())) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public enum FilterRuleType {
        START_WITH("Start with"),
        EQUALS("Equals"),
        END_WITH("End with");

        private String displayName;

        FilterRuleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String toString() {
            return getDisplayName();
        }

        public static FilterRuleType fromName(String name) {
            if (name == null)
                return START_WITH;
            for (FilterRuleType type : values()) {
                if (type.name().equals(name)) {
                    return type;
                }
            }
            return START_WITH;
        }

        public String toName() {
            return name();
        }
    }
}
