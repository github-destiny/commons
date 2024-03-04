package cn.nero.commons.generator.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
* @author Nero Claudius
* @version 1.0.0
* @since 2024/03/04
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserAuthInfoPO {

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 出生年月日
     */
    private java.time.LocalDateTime birthday;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 用户唯一标识
     */
    private Long userId;


}