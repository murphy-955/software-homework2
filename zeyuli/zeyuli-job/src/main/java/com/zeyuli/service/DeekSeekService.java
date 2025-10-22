package com.zeyuli.service;

import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Map;

public interface DeekSeekService {

    Flux<String> chat(String StartCity, String EndCity, LocalDate startDate, LocalDate endDate);
}
