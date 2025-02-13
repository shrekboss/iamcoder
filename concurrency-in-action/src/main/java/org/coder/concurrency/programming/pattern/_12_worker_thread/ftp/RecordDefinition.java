package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class RecordDefinition {
    // 2014-08-10 12:58:08.0
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
    private static final Pattern PATTERN_COMMA = Pattern.compile(",");
    private int id;
    private String productId;
    private String packageId;
    private String msisdn;

    private Date operationTime;
    private Date effectiveDate;
    private Date dueDate;

    private int operationType;

    public int targetFileIndex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RecordDefinition other = (RecordDefinition) obj;
        if (id != other.id) return false;
        return true;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public static RecordDefinition parseCsv(String line) throws ParseException {
        String[] arr = PATTERN_COMMA.split(line);
        RecordDefinition ret = new RecordDefinition();
        // ID PRODUCTID PACKAGEID MSISDN OPERATIONTIME EFFECTIVEDATE DUEDATE OPERATIONTYPE
        ret.setId(Integer.valueOf(arr[0]));
        ret.setProductId(arr[1]);
        ret.setPackageId(arr[2]);
        ret.setMsisdn(arr[3]);
        ret.setOperationTime(sdf.parse(arr[4]));
        ret.setEffectiveDate(sdf.parse(arr[5]));
        ret.setDueDate(sdf.parse(arr[6]));
        ret.setOperationType(Integer.parseInt(arr[7]));
        return ret;
    }

    @Override
    public String toString() {
        return "Record [id=" + id + ", productId=" + productId + ", packageId=" + packageId + ", msisdn=" + msisdn + ", operationTime=" + operationTime + ", effectiveDate=" + effectiveDate + ", dueDate=" + dueDate + ", operationType=" + operationType + "]";
    }

}