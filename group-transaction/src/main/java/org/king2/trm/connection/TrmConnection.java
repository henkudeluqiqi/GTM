package org.king2.trm.connection;

import org.king2.trm.TransactionType;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.pojo.TransactionPojo;
import org.king2.trm.pool.ThreadPool;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;

/**
 * =======================================================
 * 说明:  事务管理的连接
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:57          创建
 * =======================================================
 */
public class TrmConnection implements Connection {

    private Connection connection;

    public TrmConnection(Connection connection) {
        this.connection = connection;
    }

    public static Random random = new Random ();

    @Override
    public void commit() throws SQLException {

        /**
         * 最终如果一切顺利就会进入到我们commit方法当中,我们需要进行阻塞，然后等待远方的通知
         */
        // 取出ThreadLocal的信息
        TransactionPojo transactionPojo = TransactionCache.CURRENT_TD.get ();
        if (transactionPojo == null) return;

        ThreadPool.POOL.execute (new Runnable () {
            @Override
            public void run() {
                Thread.currentThread ().setName ("阻塞线程" + random.nextInt (100000));
                transactionPojo.getTask ().waitTask ();
                // 一旦被唤醒 说明服务端那边已经给出信息了 我们需要判断是回滚还是提交
                TransactionPojo cachePojo = TransactionCache.TRM_POJO_CACHE.get (transactionPojo.getTrmId ());
                try {
                    if (cachePojo.getTransactionType ().equals (TransactionType.COMMIT)) {
                        // 提交
                        connection.commit ();
                    } else {
                        // 回滚信息
                        connection.rollback ();
                    }
                    connection.close ();
                } catch (
                        SQLException e) {
                    e.printStackTrace ();
                }

            }
        });
    }

    @Override
    public void rollback() throws SQLException {
        TransactionPojo transactionPojo = TransactionCache.CURRENT_TD.get ();
        new Thread (new Runnable () {
            @Override
            public void run() {
                try {
                    transactionPojo.getTask ().waitTask ();
                    connection.rollback ();
                    connection.close ();
                } catch (SQLException e) {
                    e.printStackTrace ();
                }
            }
        }).start ();

    }

    @Override
    public void close() throws SQLException {
    }

    /**
     * 使用默认的
     */
    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement ();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement (sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall (sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL (sql);
    }


    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit ();
    }


    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed ();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData ();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly (readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly ();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog (catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog ();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation (level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation ();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings ();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings ();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement (resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement (sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall (sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap ();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap (map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability (holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability ();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint ();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint (name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback (savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint (savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.createStatement (resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareStatement (sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall (sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement (sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement (sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement (sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob ();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob ();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob ();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML ();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid (timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo (name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo (properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo (name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo ();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf (typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct (typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema (schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema ();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort (executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout (executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout ();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap (iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor (iface);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit (false);
        }
    }
}
