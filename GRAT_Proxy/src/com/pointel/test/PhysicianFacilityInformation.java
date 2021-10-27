package com.pointel.test;

public class PhysicianFacilityInformation {
	 private String firstName;
	 private String gender;
	 private String lastName;
	 private String middleName;
	 private String providerId;
	 private String suffix;
	 TaxId TaxIdObject;
	 Npi NpiObject;
	 private String organizationalType;
	 private String organizationalTypeDescription;
	 Specialty SpecialtyObject;
	 CorpOwnerInformation CorpOwnerInformationObject;
	 ContractInformation ContractInformationObject;
	 private String nhpInd;
	 private String nhpFlexInd;
	 Uhpd UhpdObject;


	 // Getter Methods 

	 public String getFirstName() {
	  return firstName;
	 }

	 public String getGender() {
	  return gender;
	 }

	 public String getLastName() {
	  return lastName;
	 }

	 public String getMiddleName() {
	  return middleName;
	 }

	 public String getProviderId() {
	  return providerId;
	 }

	 public String getSuffix() {
	  return suffix;
	 }

	 public TaxId getTaxId() {
	  return TaxIdObject;
	 }

	 public Npi getNpi() {
	  return NpiObject;
	 }

	 public String getOrganizationalType() {
	  return organizationalType;
	 }

	 public String getOrganizationalTypeDescription() {
	  return organizationalTypeDescription;
	 }

	 public Specialty getSpecialty() {
	  return SpecialtyObject;
	 }

	 public CorpOwnerInformation getCorpOwnerInformation() {
	  return CorpOwnerInformationObject;
	 }

	 public ContractInformation getContractInformation() {
	  return ContractInformationObject;
	 }

	 public String getNhpInd() {
	  return nhpInd;
	 }

	 public String getNhpFlexInd() {
	  return nhpFlexInd;
	 }

	 public Uhpd getUhpd() {
	  return UhpdObject;
	 }

	 // Setter Methods 

	 public void setFirstName(String firstName) {
	  this.firstName = firstName;
	 }

	 public void setGender(String gender) {
	  this.gender = gender;
	 }

	 public void setLastName(String lastName) {
	  this.lastName = lastName;
	 }

	 public void setMiddleName(String middleName) {
	  this.middleName = middleName;
	 }

	 public void setProviderId(String providerId) {
	  this.providerId = providerId;
	 }

	 public void setSuffix(String suffix) {
	  this.suffix = suffix;
	 }

	 public void setTaxId(TaxId taxIdObject) {
	  this.TaxIdObject = taxIdObject;
	 }

	 public void setNpi(Npi npiObject) {
	  this.NpiObject = npiObject;
	 }

	 public void setOrganizationalType(String organizationalType) {
	  this.organizationalType = organizationalType;
	 }

	 public void setOrganizationalTypeDescription(String organizationalTypeDescription) {
	  this.organizationalTypeDescription = organizationalTypeDescription;
	 }

	 public void setSpecialty(Specialty specialtyObject) {
	  this.SpecialtyObject = specialtyObject;
	 }

	 public void setCorpOwnerInformation(CorpOwnerInformation corpOwnerInformationObject) {
	  this.CorpOwnerInformationObject = corpOwnerInformationObject;
	 }

	 public void setContractInformation(ContractInformation contractInformationObject) {
	  this.ContractInformationObject = contractInformationObject;
	 }

	 public void setNhpInd(String nhpInd) {
	  this.nhpInd = nhpInd;
	 }

	 public void setNhpFlexInd(String nhpFlexInd) {
	  this.nhpFlexInd = nhpFlexInd;
	 }

	 public void setUhpd(Uhpd uhpdObject) {
	  this.UhpdObject = uhpdObject;
	 }
	}