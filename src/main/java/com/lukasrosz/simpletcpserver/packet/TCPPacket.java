package com.lukasrosz.simpletcpserver.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TCPPacket {
    private String originAddress;
    private int originPort;
    private Date receiveDate;
    private String message;

}
