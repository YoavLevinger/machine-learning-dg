package dataAccessLayer.rdb;

/**
 * Created with IntelliJ IDEA.
 * User: levinger
 */
public enum QueryType {
    SELECT, //Read Only and is part of DML
	TRANSACTIONAL_MODIFY, //DML
	DDL //Database definition language
}
