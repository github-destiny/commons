package cn.nero.commons.generator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.JDBCType;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/3/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Column {

    private String columnName;

    private String dataType;

    private JDBCType jdbcType;

    private String columnComment;

    private String columnKey;

}
