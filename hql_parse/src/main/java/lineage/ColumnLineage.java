package lineage;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import parser.base.Alias;
import parser.base.LibraryName;
import parser.base.TResultColumn;
import parser.base.TableName;

/**
 * 每个字段的血缘关系链, 由外到里
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor(staticName = "of")
public class ColumnLineage {
    /**
     * 字段所属的表的别名
     */
    private Alias alias;
    /**
     * 字段所属的表的实际表名
     */
    private TableName tableName;
    /**
     * 字段所属的表名前面的库名
     */
    private LibraryName libraryName;
    /**
     * 当前层级的字段
     */
    private TResultColumn column;

    /**
     * 下一层字段信息
     */
    private ColumnLineage nextColumnLineage;
}
