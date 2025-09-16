package ru.otus.hw.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppProperties implements TestFileNameProvider {
    private String testFileName;

    public String getTestFileName() {
        return testFileName;
    }
}
