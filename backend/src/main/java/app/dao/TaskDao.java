package app.dao;

import app.entity.Task;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Result;

import java.util.List;

/**
 * @author nsoushi
 */
@ConfigAutowireable
@Dao
public interface TaskDao {

    @Insert
    Result<Task> insert(Task record);

    @Update
    Result<Task> update(Task record);

    @Delete
    Result<Task> delete(Task record);

    @Select
    Task selectById(Long id);

    @Select
    List<Task> selectAll();

    @Select
    Integer selectCount();
}
