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
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

/**
 * druid Metrics
 *
 * @author L.cm
 */
public class DruidDataSourceMetrics implements MeterBinder {
	/**
	 * Prefix used for all Druid metric names.
	 */
	public static final String DRUID_METRIC_NAME_PREFIX = "druid";
	private static final String METRIC_CATEGORY = "pool";

	/**
	 * DataSource
	 */
	private static final String METRIC_NAME_INITIAL_SIZE = DRUID_METRIC_NAME_PREFIX + ".initial.size";
	private static final String METRIC_NAME_MIN_IDLE = DRUID_METRIC_NAME_PREFIX + ".min.idle";
	private static final String METRIC_NAME_MAX_ACTIVE = DRUID_METRIC_NAME_PREFIX + ".max.active";
	private static final String METRIC_NAME_MAX_WAIT = DRUID_METRIC_NAME_PREFIX + ".max.wait";
	private static final String METRIC_NAME_ACTIVE_COUNT = DRUID_METRIC_NAME_PREFIX + ".active.count";
	private static final String METRIC_NAME_ACTIVE_PEAK = DRUID_METRIC_NAME_PREFIX + ".active.peak";
	private static final String METRIC_NAME_POOLING_COUNT = DRUID_METRIC_NAME_PREFIX + ".pooling.count";
	private static final String METRIC_NAME_POOLING_PEAK = DRUID_METRIC_NAME_PREFIX + ".pooling.peak";
	private static final String METRIC_NAME_WAIT_THREAD_COUNT = DRUID_METRIC_NAME_PREFIX + ".wait.thread.count";
	private static final String METRIC_NAME_NOT_EMPTY_WAIT_COUNT = DRUID_METRIC_NAME_PREFIX + ".not.empty.wait.count";
	private static final String METRIC_NAME_NOT_EMPTY_WAIT_MILLIS = DRUID_METRIC_NAME_PREFIX + ".not.empty.wait.millis";
	private static final String METRIC_NAME_NOT_EMPTY_THREAD_COUNT = DRUID_METRIC_NAME_PREFIX + ".not.empty.thread.count";
	private static final String METRIC_NAME_LOGIC_CONNECT_COUNT = DRUID_METRIC_NAME_PREFIX + ".logic.connect.count";
	private static final String METRIC_NAME_LOGIC_CLOSE_COUNT = DRUID_METRIC_NAME_PREFIX + ".logic.close.count";
	private static final String METRIC_NAME_LOGIC_CONNECT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".logic.connect.error.count";
	private static final String METRIC_NAME_PHYSICAL_CONNECT_COUNT = DRUID_METRIC_NAME_PREFIX + ".physical.connect.count";
	private static final String METRIC_NAME_PHYSICAL_CLOSE_COUNT = DRUID_METRIC_NAME_PREFIX + ".physical.close.count";
	private static final String METRIC_NAME_PHYSICAL_CONNECT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".physical.connect.error.count";

	private static final String METRIC_NAME_BLOB_OPEN_COUNT = DRUID_METRIC_NAME_PREFIX + ".blob.open.count";

	private static final String METRIC_NAME_CONNECTION_ACTIVE_COUNT = DRUID_METRIC_NAME_PREFIX + ".connection.active.count";
	private static final String METRIC_NAME_CONNECTION_CONNECT_ALIVE_MILLIS = DRUID_METRIC_NAME_PREFIX + ".connection.connect.alive.millis";
	private static final String METRIC_NAME_CONNECTION_CONNECT_ALIVE_MILLIS_MIN = DRUID_METRIC_NAME_PREFIX + ".connection.connect.alive.millis.min";
	private static final String METRIC_NAME_CONNECTION_CONNECT_ALIVE_MILLIS_MAX = DRUID_METRIC_NAME_PREFIX + ".connection.connect.alive.millis.max";

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

	public DruidDataSourceMetrics(Map<String, DruidDataSource> druidDataSourceMap) {
		this.druidDataSourceMap = druidDataSourceMap;
	}

	@Override
	public void bindTo(MeterRegistry meterRegistry) {
		druidDataSourceMap.forEach((name, dataSource) -> {

			List<Tag> tags = new ArrayList<>(2);
			tags.add(Tag.of(METRIC_CATEGORY, name));

			JdbcDataSourceStat dsStats = dataSource.getDataSourceStat();

			bindDataSourceStat(meterRegistry, name, dsStats, tags);

			bindConnectionStat(meterRegistry, name, dsStats.getConnectionStat(), tags);

			bindStatementStat(meterRegistry, name, dsStats.getStatementStat(), tags);

			bindResultSetStat(meterRegistry, name, dsStats.getResultSetStat(), tags);

			bindSqlStat(meterRegistry, name, dsStats , tags);


			// basic configurations
			bindGauge(meterRegistry, METRIC_NAME_INITIAL_SIZE, "Initial size", dataSource, DruidDataSource::getInitialSize, tags);
			bindGauge(meterRegistry, METRIC_NAME_MIN_IDLE, "Min idle", dataSource, DruidDataSource::getMinIdle, tags);
			bindGauge(meterRegistry, METRIC_NAME_MAX_ACTIVE, "Max active", dataSource, DruidDataSource::getMaxActive, tags);
			bindGauge(meterRegistry, METRIC_NAME_MAX_WAIT, "Max wait", dataSource, DruidDataSource::getMaxWait, tags);

			// connection pool core metrics
			bindGauge(meterRegistry, METRIC_NAME_ACTIVE_COUNT, "Active count", dataSource, DruidDataSource::getActiveCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_ACTIVE_PEAK, "Active peak", dataSource, DruidDataSource::getActivePeak, tags);
			bindGauge(meterRegistry, METRIC_NAME_POOLING_COUNT, "Pooling count", dataSource, DruidDataSource::getPoolingCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_POOLING_PEAK, "Pooling peak", dataSource, DruidDataSource::getPoolingPeak, tags);
			bindGauge(meterRegistry, METRIC_NAME_WAIT_THREAD_COUNT, "Wait thread count", dataSource, DruidDataSource::getWaitThreadCount, tags);

			// connection pool detail metrics
			bindGauge(meterRegistry, METRIC_NAME_NOT_EMPTY_WAIT_COUNT, "Not empty wait count", dataSource, DruidDataSource::getNotEmptyWaitCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_NOT_EMPTY_WAIT_MILLIS, "Not empty wait millis", dataSource, DruidDataSource::getNotEmptyWaitMillis, tags);
			bindGauge(meterRegistry, METRIC_NAME_NOT_EMPTY_THREAD_COUNT, "Not empty thread count", dataSource, DruidDataSource::getNotEmptyWaitThreadCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_LOGIC_CONNECT_COUNT, "Logic connect count", dataSource, DruidDataSource::getConnectCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_LOGIC_CLOSE_COUNT, "Logic close count", dataSource, DruidDataSource::getCloseCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_LOGIC_CONNECT_ERROR_COUNT, "Logic connect error count", dataSource, DruidDataSource::getConnectErrorCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_PHYSICAL_CONNECT_COUNT, "Physical connect count", dataSource, DruidDataSource::getCreateCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_PHYSICAL_CLOSE_COUNT, "Physical close count", dataSource, DruidDataSource::getDestroyCount, tags);
			bindGauge(meterRegistry, METRIC_NAME_PHYSICAL_CONNECT_ERROR_COUNT, "Physical connect error count", dataSource, DruidDataSource::getCreateErrorCount, tags);

			// sql execution core metrics
			bindGauge(druidDataSource, "druid_error_count", "Error count", datasource -> (double) druidDataSource.getErrorCount());
			bindGauge(druidDataSource, "druid_execute_count", "Execute count", datasource -> (double) druidDataSource.getExecuteCount());
			// transaction metrics
			bindGauge(druidDataSource, "druid_start_transaction_count", "Start transaction count", datasource -> (double) druidDataSource.getStartTransactionCount());
			bindGauge(druidDataSource, "druid_commit_count", "Commit count", datasource -> (double) druidDataSource.getCommitCount());
			bindGauge(meterRegistry, METRIC_NAME_LOGIC_COMMIT_COUNT, "Commit count", dataSource, DruidDataSource::getCommitCount, tags);
			bindGauge(druidDataSource, "druid_rollback_count", "Rollback count", datasource -> (double) druidDataSource.getRollbackCount());

			// sql execution detail
			bindGauge(druidDataSource, "druid_prepared_statement_open_count", "Prepared statement open count", datasource -> (double) druidDataSource.getPreparedStatementCount());
			bindGauge(druidDataSource, "druid_prepared_statement_closed_count", "Prepared statement closed count", datasource -> (double) druidDataSource.getClosedPreparedStatementCount());
			bindGauge(druidDataSource, "druid_ps_cache_access_count", "PS cache access count", datasource -> (double) druidDataSource.getCachedPreparedStatementAccessCount());
			bindGauge(druidDataSource, "druid_ps_cache_hit_count", "PS cache hit count", datasource -> (double) druidDataSource.getCachedPreparedStatementHitCount());
			bindGauge(druidDataSource, "druid_ps_cache_miss_count", "PS cache miss count", datasource -> (double) druidDataSource.getCachedPreparedStatementMissCount());
			bindGauge(druidDataSource, "druid_execute_query_count", "Execute query count", datasource -> (double) druidDataSource.getExecuteQueryCount());
			bindGauge(druidDataSource, "druid_execute_update_count", "Execute update count", datasource -> (double) druidDataSource.getExecuteUpdateCount());
			bindGauge(druidDataSource, "druid_execute_batch_count", "Execute batch count", datasource -> (double) druidDataSource.getExecuteBatchCount());

			// none core metrics, some are static configurations
			bindGauge(druidDataSource, "druid_max_wait", "Max wait", datasource -> (double) druidDataSource.getMaxWait());
			bindGauge(druidDataSource, "druid_max_wait_thread_count", "Max wait thread count", datasource -> (double) druidDataSource.getMaxWaitThreadCount());
			bindGauge(druidDataSource, "druid_login_timeout", "Login timeout", datasource -> (double) druidDataSource.getLoginTimeout());
			bindGauge(druidDataSource, "druid_query_timeout", "Query timeout", datasource -> (double) druidDataSource.getQueryTimeout());
			bindGauge(druidDataSource, "druid_transaction_query_timeout", "Transaction query timeout", datasource -> (double) druidDataSource.getTransactionQueryTimeout());

		});
	}

	private void bindSqlStat(MeterRegistry registry, String datasourceName, JdbcDataSourceStat dsStats, List<Tag> tags){

	}

	private void bindDataSourceStat(MeterRegistry registry, String datasourceName, JdbcDataSourceStat dsStats, List<Tag> tags) {

		// basic configurations



		TimeGauge.builder(METRIC_NAME_CONNECTION_ACTIVE_COUNT, dsStats, TimeUnit.MILLISECONDS, JdbcDataSourceStat::getConnectionActiveCount)
				.description("Connection Active Count")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		TimeGauge.builder(METRIC_NAME_CONNECTION_CONNECT_ALIVE_MILLIS, dsStats, TimeUnit.MILLISECONDS, JdbcDataSourceStat::getConnectionConnectAliveMillis)
				.description("Connection Connect Alive Millis")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		TimeGauge.builder(METRIC_NAME_CONNECTION_CONNECT_ALIVE_MILLIS_MIN, dsStats, TimeUnit.MILLISECONDS, JdbcDataSourceStat::getConnectionConnectAliveMillisMin)
				.description("Connection Connect Alive Millis Min")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);
		TimeGauge.builder(METRIC_NAME_CONNECTION_CONNECT_ALIVE_MILLIS_MAX, dsStats, TimeUnit.MILLISECONDS, JdbcDataSourceStat::getConnectionConnectAliveMillisMax)
				.description("Connection Connect Alive Millis Max")
				.tags(tags)
				.tag(METRIC_CATEGORY, datasourceName)
				.register(registry);

		bindCounter(registry, METRIC_NAME_BLOB_OPEN_COUNT, "The number of  requests of dispatcher by per host ", dsStats, JdbcDataSourceStat::getBlobOpenCount);


	}

	private void bindConnectionStat(MeterRegistry registry, String datasourceName, JdbcConnectionStat connectionStat, List<Tag> tags) {

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

	private void bindStatementStat(MeterRegistry registry, String datasourceName, JdbcStatementStat statementStat, List<Tag> tags) {

		bindGauge(registry, METRIC_NAME_STATEMENT_CREATE_COUNT, "Jdbc Statement count", statementStat, JdbcStatementStat::getErrorCount);

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


	}
/*

	private static final String METRIC_NAME_STATEMENT_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.count";
	private static final String METRIC_NAME_STATEMENT_CREATE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.create.count";
	private static final String METRIC_NAME_STATEMENT_PREPARE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.prepare.count";
	private static final String METRIC_NAME_STATEMENT_PREPARE_CALL_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.prepare.call.count";
	private static final String METRIC_NAME_STATEMENT_CLOSE_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.close.count";
	private static final String METRIC_NAME_STATEMENT_RUNNING_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.running.count";
	private static final String METRIC_NAME_STATEMENT_CONCURRENT_MAX = DRUID_METRIC_NAME_PREFIX + ".statement.concurrent.max";
	private static final String METRIC_NAME_STATEMENT_ERROR_COUNT = DRUID_METRIC_NAME_PREFIX + ".statement.error.count";
	private static final String METRIC_NAME_STATEMENT_NANO_TOTAL = DRUID_METRIC_NAME_PREFIX + ".statement.nano.total";*/

	private void bindResultSetStat(MeterRegistry registry, String name, JdbcResultSetStat resultSetStat, List<Tag> tags) {

	}

	private <T> void bindGauge(MeterRegistry registry, String metric, String help, T measureObj, ToDoubleFunction<T> measure, Iterable<Tag> tags) {
		Gauge.builder(metric, measureObj, measure)
				.description(help)
				.tags(tags)
				.register(registry);
	}

	private <T> void bindTimeGauge(MeterRegistry registry, String metric, String help, T metricResult,
								   ToDoubleFunction<T> measure, Iterable<Tag> tags) {
		TimeGauge.builder(metric, metricResult, TimeUnit.SECONDS, measure)
				.description(help)
				.tags(tags)
				.register(registry);
	}

	private <T> void bindTimer(MeterRegistry registry, String metric, String help, T measureObj,
							   ToLongFunction<T> countFunc, ToDoubleFunction<T> measure, Iterable<Tag> tags) {
		FunctionTimer.builder(metric, measureObj, countFunc, measure, TimeUnit.SECONDS)
				.description(help)
				.tags(tags)
				.register(registry);
	}


	private <T> void bindCounter(MeterRegistry registry, String name, String help, T measureObj,
								 ToDoubleFunction<T> measure, Iterable<Tag> tags) {
		FunctionCounter.builder(name, measureObj, measure)
				.description(help)
				.tags(tags)
				.register(registry);
	}

}
