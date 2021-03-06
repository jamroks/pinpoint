/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.web.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.web.vo.Application;

/**
 *
 */
@Component
public class ApplicationNameMapper implements RowMapper<List<Application>> {

    @Autowired
    private ServiceTypeRegistryService registry;

    @Override
    public List<Application> mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Short> uniqueTypeCodes = new HashSet<Short>();
        String applicationName = Bytes.toString(result.getRow());
        
        List<KeyValue> list = result.list();
        for(KeyValue value :list) {
            short serviceTypeCode = Bytes.toShort(value.getValue());
            uniqueTypeCodes.add(serviceTypeCode);
        }
        List<Application> applications = new ArrayList<Application>();
        for (short serviceTypeCode : uniqueTypeCodes) {
            applications.add(new Application(applicationName, registry.findServiceType(serviceTypeCode)));
        }
        return applications;
    }
}
