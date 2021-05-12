package pres.cocoadel.web.mvc;

import pres.cocoadel.web.mvc.controller.Controller;

import java.lang.invoke.MethodHandle;

public interface MethodHandleMapping {

    Controller findController(String requestMappingPath);

    MethodHandle findMethodHandle(String requestMappingPath);
}
