/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.druid.metrics;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DruidDataSourceMetadata Provide
 *
 * @author L.cm
 * @author DL.Wan
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({ MetricsAutoConfiguration.class, DataSourceAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class })
@ConditionalOnClass({ DruidDataSource.class, MeterRegistry.class })
@ConditionalOnBean({ DataSource.class, MeterRegistry.class })
public class DruidMetricsAutoConfiguration {

	private static final String DATASOURCE_SUFFIX = "dataSource";

	@Bean
	public DataSourcePoolMetadataProvider druidDataSourceMetadataProvider() {
		return (dataSource) -> {
			DruidDataSource druidDataSource = DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
			if (druidDataSource != null) {
				return new DruidDataSourcePoolMetadata(druidDataSource);
			}
			return null;
		};
	}

	@Bean
	@ConditionalOnMissingBean
	public StatFilter statFilter() {
		return new StatFilter();
	}

	private final MeterRegistry registry;

	DruidMetricsAutoConfiguration(MeterRegistry registry) {
		this.registry = registry;
	}

	@Autowired
	void bindMetricsRegistryToDruidDataSources(Map<String, DataSource> dataSources, MeterRegistry registry) {
		dataSources.forEach(
				(name, dataSource) -> bindMetricsRegistryToDruidDataSource(name, dataSource, registry));

		for (DataSource dataSource : dataSources) {
			DruidDataSource druidDataSource = DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
			if (druidDataSource != null) {
				bindMetricsRegistryToDruidDataSource(druidDataSource);
			}
					ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders
		}
	}

	private void bindMetricsRegistryToDruidDataSource(DruidDataSource druid) {

	}

	@Bean
	public DruidDataSourceMetrics druidMetrics(ObjectProvider<Map<String, DataSource>> dataSourcesProvider) {
		Map<String, DataSource> dataSourceMap = dataSourcesProvider.getIfAvailable(HashMap::new);
		Map<String, DruidDataSource> druidDataSourceMap = new HashMap<>(2);
		dataSourceMap.forEach((name, dataSource) -> {
			// 保证连接池数据和 DataSourcePoolMetadataProvider 的一致
			DruidDataSource druidDataSource = DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
			if (druidDataSource != null) {
				druidDataSourceMap.put(getDataSourceName(name), druidDataSource);
			}
		});
		return druidDataSourceMap.isEmpty() ? null : new DruidDataSourceMetrics(druidDataSourceMap);
	}

	/**
	 * Get the name of a DataSource based on its {@code beanName}.
	 *
	 * @param beanName the name of the data source bean
	 * @return a name for the given data source
	 */
	private static String getDataSourceName(String beanName) {
		if (beanName.length() > DATASOURCE_SUFFIX.length()
			&& StringUtils.endsWithIgnoreCase(beanName, DATASOURCE_SUFFIX)) {
			return beanName.substring(0, beanName.length() - DATASOURCE_SUFFIX.length());
		}
		return beanName;
	}

}
