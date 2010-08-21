package uk.ac.dundee.computing.aec.utils;
import me.prettyprint.cassandra.model.*;
import org.apache.cassandra.thrift.ConsistencyLevel;

public final class MyConsistancyLevel implements ConsistencyLevelPolicy {

	@Override
	  public  ConsistencyLevel get(OperationType op) {
		switch (op){
		case READ:return ConsistencyLevel.QUORUM;
		case WRITE: return ConsistencyLevel.ONE;
		default: return ConsistencyLevel.QUORUM; //Just in Case
				
		}

	  }

	  @Override
	  public ConsistencyLevel get(OperationType op, String cfName) {
		  switch (op){
		  case READ:return ConsistencyLevel.QUORUM;
		  case WRITE: return ConsistencyLevel.ONE;
		  default: return ConsistencyLevel.QUORUM; //Just in Case
		  }
	  }

	}

