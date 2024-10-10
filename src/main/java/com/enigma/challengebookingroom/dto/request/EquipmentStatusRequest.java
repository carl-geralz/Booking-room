package com.enigma.challengebookingroom.dto.request;

import java.io.Serializable;

import com.enigma.challengebookingroom.constant.ConstantEquipmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentStatusRequest implements Serializable {
    String equipmentStatusId;
    ConstantEquipmentStatus equipmentStatusName;
}