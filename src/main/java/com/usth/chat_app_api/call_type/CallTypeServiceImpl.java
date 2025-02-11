package com.usth.chat_app_api.call_type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallTypeServiceImpl implements ICallTypeService{
    @Autowired
    CallTypeRepository repo;
}
