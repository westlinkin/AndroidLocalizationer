/*
 * Copyright 2014 Wesley Lin
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
import data.Log;
import data.StorageDataKey;
import org.apache.commons.lang.SerializationUtils;

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
        return getFilterRuleType().name() + "<" + getFilterString() + ">";
    }

    public static List<FilterRule> getFilterRulesFromLocal() {
        List<FilterRule> result = new ArrayList<FilterRule>();

        String rules = PropertiesComponent.getInstance().getValue(StorageDataKey.SettingFilterRules);
        if (rules == null) {
            result.add(DefaultFilterRule);
        } else {
            Log.i("rules: " + rules);
            //
            Object deserialize = SerializationUtils.deserialize(rules.getBytes());
            Log.i("deserialize: " + deserialize);
            //todo
        }
        return result;
    }

    public enum FilterRuleType {
        START_WITH,
        EQUALS,
        END_WITH
    }
}
