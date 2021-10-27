package com.pointel.test;

public class Uhpd {
	 Efficiency EfficiencyObject;
	 private String focusCode;
	 private String focusDescription;
	 private String qeDesignationPriority;
	 private String qeDesignationTierNumber;
	 Quality QualityObject;


	 // Getter Methods 

	 public Efficiency getEfficiency() {
	  return EfficiencyObject;
	 }

	 public String getFocusCode() {
	  return focusCode;
	 }

	 public String getFocusDescription() {
	  return focusDescription;
	 }

	 public String getQeDesignationPriority() {
	  return qeDesignationPriority;
	 }

	 public String getQeDesignationTierNumber() {
	  return qeDesignationTierNumber;
	 }

	 public Quality getQuality() {
	  return QualityObject;
	 }

	 // Setter Methods 

	 public void setEfficiency(Efficiency efficiencyObject) {
	  this.EfficiencyObject = efficiencyObject;
	 }

	 public void setFocusCode(String focusCode) {
	  this.focusCode = focusCode;
	 }

	 public void setFocusDescription(String focusDescription) {
	  this.focusDescription = focusDescription;
	 }

	 public void setQeDesignationPriority(String qeDesignationPriority) {
	  this.qeDesignationPriority = qeDesignationPriority;
	 }

	 public void setQeDesignationTierNumber(String qeDesignationTierNumber) {
	  this.qeDesignationTierNumber = qeDesignationTierNumber;
	 }

	 public void setQuality(Quality qualityObject) {
	  this.QualityObject = qualityObject;
	 }
	}