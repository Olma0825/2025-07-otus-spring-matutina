package ru.otus.hw.dao;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    private final ResourceLoader resourceLoader;

    @SuppressWarnings("checkstyle:LineLength")
    @Override
    public List<Question> findAll() {
        Resource resource = new ClassPathResource(fileNameProvider.getTestFileName());

        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            ColumnPositionMappingStrategy<QuestionDto> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(QuestionDto.class);
            String[] columns = {"text", "answers"};
            strategy.setColumnMapping(columns);

            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .withMappingStrategy(strategy)
                    .build();

            List<QuestionDto> beans =  csvToBean.parse();
            return beans.stream().map(QuestionDto::toDomainObject).toList();
        } catch (Exception e) {
            throw new QuestionReadException("Error when reading CSV: " + e.getMessage(), e);
        }
    }
}