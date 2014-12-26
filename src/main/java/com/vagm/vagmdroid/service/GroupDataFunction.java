package com.vagm.vagmdroid.service;

import com.vagm.vagmdroid.dto.DataStreamDTO;

public interface GroupDataFunction {
    DataStreamDTO getDataStreamDTO(int blockData1, int blockData2);
}
