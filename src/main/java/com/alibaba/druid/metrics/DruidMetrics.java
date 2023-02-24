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

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcConnectionStat;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcResultSetStat;
import com.alibaba.druid.stat.JdbcStatementStat;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

/**
 * druid Metrics
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class DruidMetrics implements MeterBinder {
	/**
	 * Prefix used for all Druid metric names.
	 */
	public static final String DRUID_METRIC_NAME_PREFIX = "druid";

	private static final String METRIC_CATEGORY = "name";
	/**
	 * connections
	 */
	private static final String METRIC_NAME_CONNECTORS_CONNECT_MAX_TIME = DRUID_METRIC_NAME_PREFIX + ".connections.connect.max.time";
	private static final String METRIC_NAME_CONNECTORS_ALIVE_MAX_TIME = DRUID_METRIC_NAME_PREFIX + ".connections.alive.max.time";
	private static final String METRIC_NAME_CONNECTORS_ALIVE_MIN_TIME = DRUID_METRIC_NAME_PREFIX + ".connections.alive.min.time";
	private static final String METRIC_NAME_CONNECTORS_CONNECT_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.connect.count";
	private static final String METRIC_NAME_CONNECTORS_ACTIVE_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.active.count";
	private static final String METRIC_NAME_CONNECTORS_CLOSE_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.close.count";
	private static final String METRIC_NAME_CONNECTORS_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.error.count";
	private static final String METRIC_NAME_CONNECTORS_CONNECT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.connect.error.count";
	private static final String METRIC_NAME_CONNECTORS_COMMIT_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.commit.count";
	private static final String METRIC_NAME_CONNECTORS_ROLLBACK_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.rollback.count";
	/**
	 * statement
	 */
	private static final String METRIC_NAME_STATEMENT_CREATE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.create.count";
	private static final String METRIC_NAME_STATEMENT_PREPARE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.prepare.count";
	private static final String METRIC_NAME_STATEMENT_PREPARE_CALL_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.prepare.call.count";
	private static final String METRIC_NAME_STATEMENT_CLOSE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.close.count";
	private static final String METRIC_NAME_STATEMENT_RUNNING_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.running.count";
	private static final String METRIC_NAME_STATEMENT_CONCURRENT_MAX = DRUID_METRIC_NAME_PREFIX + ".statement.concurrent.max";
	private static final String METRIC_NAME_STATEMENT_EXECUTE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.execute.count";
	private static final String METRIC_NAME_STATEMENT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.error.count";
	private static final String METRIC_NAME_STATEMENT_NANO_TOTAL = DRUID_METRIC_NAME_PREFIX + ".statement.nano.total";
	/**
	 * resultSet
	 */
	private static final String METRIC_NAME_RESULTSET_CONNECT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.connect.error.count";
	private static final String METRIC_NAME_RESULTSET_COMMIT_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.commit.count";
	private static final String METRIC_NAME_RESULTSET_ROLLBACK_COUNT = DRUID_METRIC_NAME_PREFIX + ".connections.rollback.count";

	private final Map<String, DruidDataSource> druidDataSourceMap;
	private final Iterable<Tag> tags;

	public DruidMetrics(Map<String, DruidDataSource> druidDataSourceMap) {
		this(druidDataSourceMap, Collections.emptyList());
	}

	@Override
	public void bindTo(MeterRegistry meterRegistry) {
		druidDataSourceMap.forEach((name, dataSource) -> {
			JdbcDataSourceStat dsStats = dataSource.getDataSourceStat();

			bindConnectionStat(meterRegistry, name, dsStats.getConnectionStat());

			bindStatementStat(meterRegistry, name, dsStats.getStatementStat());

			bindResultSetStat(meterRegistry, name, dsStats.getResultSetStat());
		});
	}

	private void bindConnectionStat(MeterRegistry registry, String datasourceName, JdbcConnectionStat connectionStat) {
		// time
		Gauge.builder(METRIC_NAME_CONNECTORS_CONNECT_MAX_TIME, connectionStat, JdbcConnectionStat::getConnectMillisMax)
				.description("Connection connect max time")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.baseUnit(BaseUnits.MILLISECONDS)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_ALIVE_MAX_TIME, connectionStat, JdbcConnectionStat::getAliveMillisMax)
				.description("Connection alive max time")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.baseUnit(BaseUnits.MILLISECONDS)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_ALIVE_MIN_TIME, connectionStat, JdbcConnectionStat::getAliveMillisMin)
				.description("Connection alive min time")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.baseUnit(BaseUnits.MILLISECONDS)
				.register(registry);
		// count
		Gauge.builder(METRIC_NAME_CONNECTORS_ACTIVE_COUNT, connectionStat, JdbcConnectionStat::getActiveCount)
				.description("Connection active count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_CONNECT_COUNT, connectionStat, JdbcConnectionStat::getConnectCount)
				.description("Connection connect count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_CLOSE_COUNT, connectionStat, JdbcConnectionStat::getCloseCount)
				.description("Connection close count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_ERROR_COUNT, connectionStat, JdbcConnectionStat::getErrorCount)
				.description("Connection error count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_CONNECT_ERROR_COUNT, connectionStat, JdbcConnectionStat::getConnectErrorCount)
				.description("Connection connect error count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_COMMIT_COUNT, connectionStat, JdbcConnectionStat::getCommitCount)
				.description("Connecting commit count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		Gauge.builder(METRIC_NAME_CONNECTORS_ROLLBACK_COUNT, connectionStat, JdbcConnectionStat::getRollbackCount)
				.description("Connection rollback count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
	}

	private void bindStatementStat(MeterRegistry registry, String datasourceName, JdbcStatementStat statementStat) {

		Gauge.builder(METRIC_NAME_STATEMENT_CREATE_COUNT, statementStat, JdbcStatementStat::getCreateCount)
				.description("Jdbc Statement Create count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);

		Gauge.builder(METRIC_NAME_STATEMENT_EXECUTE_COUNT, statementStat, JdbcStatementStat::getExecuteCount)
				.description("Connection active count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);

		private static final String METRIC_NAME_STATEMENT_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.count";
		private static final String METRIC_NAME_STATEMENT_CREATE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.create.count";
		private static final String METRIC_NAME_STATEMENT_PREPARE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.prepare.count";
		private static final String METRIC_NAME_STATEMENT_PREPARE_CALL_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.prepare.call.count";
		private static final String METRIC_NAME_STATEMENT_CLOSE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.close.count";
		private static final String METRIC_NAME_STATEMENT_RUNNING_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.running.count";
		private static final String METRIC_NAME_STATEMENT_CONCURRENT_MAX = DRUID_METRIC_NAME_PREFIX + ".statement.concurrent.max";
		private static final String METRIC_NAME_STATEMENT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.error.count";
		private static final String METRIC_NAME_STATEMENT_NANO_TOTAL = DRUID_METRIC_NAME_PREFIX + ".statement.nano.total";

	}

	private void bindResultSetStat(MeterRegistry registry, String name, JdbcResultSetStat resultSetStat) {

	}

}
