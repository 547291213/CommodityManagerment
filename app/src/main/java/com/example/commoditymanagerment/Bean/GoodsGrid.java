package com.example.commoditymanagerment.Bean;

import java.util.List;

public class GoodsGrid {

    private int current;//当前页面号
    private int rowCount;//每页行数
    private int total;//总行数
    private List<Goods> rows; //商品信息列表

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Goods> getRows() {
        return rows;
    }

    public void setRows(List<Goods> rows) {
        this.rows = rows;
    }

    public String string(){
        return "current:"+current + "\nrowCount:"+rowCount+"\nTotal:" +total ;
    }
}
