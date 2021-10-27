package com.pointel.test;

public class PhysicianFacilitySummary0001Response {
	 Metadata MetadataObject;
	 SvcResponse SvcResponseObject;
	 private String ServiceFault;


	 // Getter Methods 

	 public Metadata getMetadata() {
	  return MetadataObject;
	 }

	 public SvcResponse getSvcResponse() {
	  return SvcResponseObject;
	 }

	 public String getServiceFault() {
	  return ServiceFault;
	 }

	 // Setter Methods 

	 public void setMetadata(Metadata metadataObject) {
	  this.MetadataObject = metadataObject;
	 }

	 public void setSvcResponse(SvcResponse svcResponseObject) {
	  this.SvcResponseObject = svcResponseObject;
	 }

	 public void setServiceFault(String ServiceFault) {
	  this.ServiceFault = ServiceFault;
	 }
	}