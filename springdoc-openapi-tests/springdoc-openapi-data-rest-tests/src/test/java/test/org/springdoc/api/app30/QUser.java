package test.org.springdoc.api.app30;


import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QUser is a Querydsl query type for User
 */
public class QUser extends EntityPathBase<User> {

	public static final QUser user = new QUser("user");

	private static final long serialVersionUID = 222331676L;

	public final StringPath email = createString("email");

	public final StringPath firstName = createString("firstName");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final StringPath lastName = createString("lastName");

	public QUser(String variable) {
		super(User.class, forVariable(variable));
	}

	public QUser(Path<? extends User> path) {
		super(path.getType(), path.getMetadata());
	}

	public QUser(PathMetadata metadata) {
		super(User.class, metadata);
	}

}