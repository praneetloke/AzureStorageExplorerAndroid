package com.pl.azurestorageexplorer.models;

import java.io.Serializable;

/**
 * Created by Praneet Loke on 7/2/2016.
 */
public class Subscription implements Serializable {
    private String SubscriptionStatus;
    private String CreatedTime;
    private String MaxHostedServices;
    private String SubscriptionName;
    private String MaxDnsServers;
    private String ServiceAdminLiveEmailId;
    private String CurrentHostedServices;
    private String CurrentCoreCount;
    private String MaxCoreCount;
    private String MaxLocalNetworkSites;
    private String CurrentStorageAccounts;
    private String CurrentVirtualNetworkSites;
    private String SubscriptionID;
    private String MaxStorageAccounts;
    private String MaxVirtualNetworkSites;
    private String AccountAdminLiveEmailId;
    private String AADTenantID;

    public Subscription() {

    }

    public String getSubscriptionStatus() {
        return SubscriptionStatus;
    }

    public void setSubscriptionStatus(String SubscriptionStatus) {
        this.SubscriptionStatus = SubscriptionStatus;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String CreatedTime) {
        this.CreatedTime = CreatedTime;
    }

    public String getMaxHostedServices() {
        return MaxHostedServices;
    }

    public void setMaxHostedServices(String MaxHostedServices) {
        this.MaxHostedServices = MaxHostedServices;
    }

    public String getSubscriptionName() {
        return SubscriptionName;
    }

    public void setSubscriptionName(String SubscriptionName) {
        this.SubscriptionName = SubscriptionName;
    }

    public String getMaxDnsServers() {
        return MaxDnsServers;
    }

    public void setMaxDnsServers(String MaxDnsServers) {
        this.MaxDnsServers = MaxDnsServers;
    }

    public String getServiceAdminLiveEmailId() {
        return ServiceAdminLiveEmailId;
    }

    public void setServiceAdminLiveEmailId(String ServiceAdminLiveEmailId) {
        this.ServiceAdminLiveEmailId = ServiceAdminLiveEmailId;
    }

    public String getCurrentHostedServices() {
        return CurrentHostedServices;
    }

    public void setCurrentHostedServices(String CurrentHostedServices) {
        this.CurrentHostedServices = CurrentHostedServices;
    }

    public String getCurrentCoreCount() {
        return CurrentCoreCount;
    }

    public void setCurrentCoreCount(String CurrentCoreCount) {
        this.CurrentCoreCount = CurrentCoreCount;
    }

    public String getMaxCoreCount() {
        return MaxCoreCount;
    }

    public void setMaxCoreCount(String MaxCoreCount) {
        this.MaxCoreCount = MaxCoreCount;
    }

    public String getMaxLocalNetworkSites() {
        return MaxLocalNetworkSites;
    }

    public void setMaxLocalNetworkSites(String MaxLocalNetworkSites) {
        this.MaxLocalNetworkSites = MaxLocalNetworkSites;
    }

    public String getCurrentStorageAccounts() {
        return CurrentStorageAccounts;
    }

    public void setCurrentStorageAccounts(String CurrentStorageAccounts) {
        this.CurrentStorageAccounts = CurrentStorageAccounts;
    }

    public String getCurrentVirtualNetworkSites() {
        return CurrentVirtualNetworkSites;
    }

    public void setCurrentVirtualNetworkSites(String CurrentVirtualNetworkSites) {
        this.CurrentVirtualNetworkSites = CurrentVirtualNetworkSites;
    }

    public String getSubscriptionID() {
        return SubscriptionID;
    }

    public void setSubscriptionID(String SubscriptionID) {
        this.SubscriptionID = SubscriptionID;
    }

    public String getMaxStorageAccounts() {
        return MaxStorageAccounts;
    }

    public void setMaxStorageAccounts(String MaxStorageAccounts) {
        this.MaxStorageAccounts = MaxStorageAccounts;
    }

    public String getMaxVirtualNetworkSites() {
        return MaxVirtualNetworkSites;
    }

    public void setMaxVirtualNetworkSites(String MaxVirtualNetworkSites) {
        this.MaxVirtualNetworkSites = MaxVirtualNetworkSites;
    }

    public String getAccountAdminLiveEmailId() {
        return AccountAdminLiveEmailId;
    }

    public void setAccountAdminLiveEmailId(String AccountAdminLiveEmailId) {
        this.AccountAdminLiveEmailId = AccountAdminLiveEmailId;
    }

    public String getAADTenantID() {
        return AADTenantID;
    }

    public void setAADTenantID(String AADTenantID) {
        this.AADTenantID = AADTenantID;
    }

    @Override
    public String toString() {
        return "ClassPojo [SubscriptionStatus = " + SubscriptionStatus + ", CreatedTime = " + CreatedTime + ", MaxHostedServices = " + MaxHostedServices + ", SubscriptionName = " + SubscriptionName + ", MaxDnsServers = " + MaxDnsServers + ", ServiceAdminLiveEmailId = " + ServiceAdminLiveEmailId + ", CurrentHostedServices = " + CurrentHostedServices + ", CurrentCoreCount = " + CurrentCoreCount + ", MaxCoreCount = " + MaxCoreCount + ", MaxLocalNetworkSites = " + MaxLocalNetworkSites + ", CurrentStorageAccounts = " + CurrentStorageAccounts + ", CurrentVirtualNetworkSites = " + CurrentVirtualNetworkSites + ", SubscriptionID = " + SubscriptionID + ", MaxStorageAccounts = " + MaxStorageAccounts + ", MaxVirtualNetworkSites = " + MaxVirtualNetworkSites + ", AccountAdminLiveEmailId = " + AccountAdminLiveEmailId + ", AADTenantID = " + AADTenantID + "]";
    }
}
