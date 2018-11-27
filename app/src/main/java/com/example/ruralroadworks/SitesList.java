package com.example.ruralroadworks;

import java.util.ArrayList;

/** Contains getter and setter method for varialbles  */
public class SitesList {

	/** Variables */
	public ArrayList<String> photo = new ArrayList<String>();
	public ArrayList<String> emp_name = new ArrayList<String>();
	public ArrayList<String> emp_designation = new ArrayList<String>();
	public ArrayList<String> pendingWork = new ArrayList<String>();
	public ArrayList<String> emp_lastVisitDate = new ArrayList<String>();
	public ArrayList<String> ServiceProvider = new ArrayList<String>();
	
	public ArrayList<String> id = new ArrayList<String>();
	public ArrayList<String> work_id = new ArrayList<String>();
	public ArrayList<String> scheme = new ArrayList<String>();
	public ArrayList<String> financialyear = new ArrayList<String>();
	public ArrayList<String> agencyname = new ArrayList<String>();
	public ArrayList<String> workname = new ArrayList<String>();
	public ArrayList<String> block = new ArrayList<String>();
	public ArrayList<String> village = new ArrayList<String>();
	public ArrayList<String> data = new ArrayList<String>();
	
	/** In Setter method default it will return arraylist 
	 *  change that to add  */

	public ArrayList<String> getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo.add(photo);
	}
	
	
	public ArrayList<String> getEmpName() {
		return emp_name;
	}
	public void setEmpName(String emp_name) {
		this.emp_name.add(emp_name);
	}

	
	public ArrayList<String> getEmpDesignation() {
		return emp_designation;
	}
	public void setEmpDesignation(String emp_designation) {
		this.emp_designation.add(emp_designation);
	}
	
	
	public ArrayList<String> getPendingWork() {
		return pendingWork;
	}
	public void setPendingWork(String pendingWork) {
		this.pendingWork.add(pendingWork);
	}
	
	
	public ArrayList<String> getLastVisitDate() {
		return emp_lastVisitDate;
	}
	public void setLastVisitDate(String emp_lastVisitDate) {
		this.emp_lastVisitDate.add(emp_lastVisitDate);
	}
	
	
	public ArrayList<String> getServiceProvider() {
		return ServiceProvider;
	}
	public void setServiceProvider(String ServiceProvider) {
		this.ServiceProvider.add(ServiceProvider);
	}
	
	
	public ArrayList<String> getId() {
		return id;
	}
	public void setId(String Id) {
		this.id.add(Id);
	}
	
	public ArrayList<String> getWorkId() {
		return work_id;
	}
	public void setWorkId(String work_id) {
		this.work_id.add(work_id);
	}
	
	public ArrayList<String> getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme.add(scheme);
	}

	
	public ArrayList<String> getFinancialyear() {
		return financialyear;
	}
	public void setFinancialyear(String financialyear) {
		this.financialyear.add(financialyear);
	}
	
	
	public ArrayList<String> getAgencyName() {
		return agencyname;
	}
	public void setAgencyName(String agencyname) {
		this.agencyname.add(agencyname);
	}
	
	
	public ArrayList<String> getWorkName() {
		return workname;
	}
	public void setWorkName(String workname) {
		this.workname.add(workname);
	}
	
	
	public ArrayList<String> getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block.add(block);
	}
	
	
	public ArrayList<String> getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village.add(village);
	}
	
	public ArrayList<String> getData() {
		return data;
	}
	public void setData(String data) {
		this.data.add(data);
	}
	
}
