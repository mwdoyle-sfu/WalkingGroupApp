package com.teal.a276.walkinggroup.model.dataobjects.permissions;

/**
 * Different status of a permission request
 */

public enum PermissionStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DECLINED("DENIED");

    final String value;

    PermissionStatus(String status) {
        this.value = status;
    }

    public String getValue() {
        return value;
    }


}
