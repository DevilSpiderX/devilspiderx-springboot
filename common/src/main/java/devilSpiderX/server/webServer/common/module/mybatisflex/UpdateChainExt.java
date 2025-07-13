package devilSpiderX.server.webServer.common.module.mybatisflex;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.mybatis.Mappers;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.LambdaGetter;
import lombok.Getter;

/**
 * 增强{@link UpdateChain}，多了个属性{@link UpdateChainExt#modified}用于记录是否成功set
 *
 * @author DevilSpiderX
 */
@Getter
@SuppressWarnings("unchecked")
public class UpdateChainExt<T> extends UpdateChain<T> {

    private boolean modified = false;

    public static <T> UpdateChainExt<T> of(Class<T> entityClass) {
        BaseMapper<T> baseMapper = Mappers.ofEntityClass(entityClass);
        return new UpdateChainExt<>(baseMapper);
    }

    public static <T> UpdateChainExt<T> of(BaseMapper<T> baseMapper) {
        return new UpdateChainExt<>(baseMapper);
    }

    public static <T> UpdateChainExt<T> of(T entityObject) {
        Class<T> entityClass = (Class<T>) ClassUtil.getUsefulClass(entityObject.getClass());
        BaseMapper<T> baseMapper = Mappers.ofEntityClass(entityClass);
        return new UpdateChainExt<>(baseMapper, entityObject);
    }

    public static <T> UpdateChainExt<T> of(T entityObject, BaseMapper<T> baseMapper) {
        return new UpdateChainExt<>(baseMapper, entityObject);
    }

    public UpdateChainExt(final BaseMapper<T> baseMapper) {
        super(baseMapper);
    }

    public UpdateChainExt(final BaseMapper<T> baseMapper, final T entityObject) {
        super(baseMapper, entityObject);
    }

    @Override
    public UpdateChain<T> set(final String property, final Object value, final boolean isEffective) {
        if (isEffective) {
            modified = true;
        }
        return super.set(property, value, isEffective);
    }

    @Override
    public UpdateChain<T> set(final QueryColumn queryColumn, final Object value, final boolean isEffective) {
        if (isEffective) {
            modified = true;
        }
        return super.set(queryColumn, value, isEffective);
    }

    @Override
    public <L> UpdateChain<T> set(final LambdaGetter<L> getter, final Object value, final boolean isEffective) {
        if (isEffective) {
            modified = true;
        }
        return super.set(getter, value, isEffective);
    }

    @Override
    public UpdateChain<T> setRaw(final String property, final Object value, final boolean isEffective) {
        if (isEffective) {
            modified = true;
        }
        return super.setRaw(property, value, isEffective);
    }

    @Override
    public UpdateChain<T> setRaw(final QueryColumn queryColumn, final Object value, final boolean isEffective) {
        if (isEffective) {
            modified = true;
        }
        return super.setRaw(queryColumn, value, isEffective);
    }

    @Override
    public <L> UpdateChain<T> setRaw(final LambdaGetter<L> getter, final Object value, final boolean isEffective) {
        if (isEffective) {
            modified = true;
        }
        return super.setRaw(getter, value, isEffective);
    }
}
