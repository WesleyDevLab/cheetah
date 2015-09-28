package com.zhaijiong.stock.common;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zhaijiong.stock.model.StockData;

import java.util.Set;

import static com.zhaijiong.stock.common.Conditions.Operation.*;

/**
 * author: xuqi.xq
 * date: 15-9-28.
 */
public class Conditions {
    public enum Operation{
        GT,LT,EQ
    }

    private Table<String,Operation,Double> conditions;

    public Conditions(){
        conditions = HashBasedTable.create();
    }

    public void addCondition(String name,Operation op,Double value){
        conditions.put(name,op,value);
    }

    public boolean check(StockData stockData){
        Set<Table.Cell<String, Operation, Double>> cells = conditions.cellSet();
        for(Table.Cell<String,Operation,Double> cell:cells){
            Double val = stockData.get(cell.getRowKey());
            if(val!=null){

                switch (cell.getColumnKey()){
                    case GT:
                        if (val<=cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                    case LT:
                        if(val >= cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                    case EQ:
                        if(val!=cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                }
            }else{
                return false;
            }

        }
        return true;
    }
}
