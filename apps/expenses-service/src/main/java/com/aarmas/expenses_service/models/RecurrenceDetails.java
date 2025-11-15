package com.aarmas.expenses_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class RecurrenceDetails {
    private RecurrenceType type;
    private LocalDate startDate;
    private LocalDate endDate;
}