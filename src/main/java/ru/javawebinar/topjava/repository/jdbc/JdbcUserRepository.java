package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.*;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

//    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private static final ResultSetExtractor<List<User>> RSE = rs -> {

        Map<Integer, User> users = new LinkedHashMap<>();
        while (rs.next()) {
            User newUser = new User();
            int id = rs.getInt("id");

            if (!users.containsKey(id)) {
                newUser.setId(rs.getInt("id"));
                newUser.setName(rs.getString("name"));
                newUser.setEmail(rs.getString("email"));
                newUser.setPassword(rs.getString("password"));
                newUser.setCaloriesPerDay(rs.getInt("calories_per_day"));
                newUser.setEnabled(rs.getBoolean("enabled"));
                newUser.setRegistered(rs.getDate("registered"));

                String role = rs.getString("role");
                newUser.setRoles(role != null ? List.of(Role.valueOf(role)) : Collections.emptyList());

                users.put(id, newUser);
            } else {
                User user = users.get(id);
                Set<Role> roles = user.getRoles();
                roles.add(Role.valueOf(rs.getString("role")));
            }
        }
        return new ArrayList<>(users.values());
    };

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final Validator validator;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    @Transactional
    public User save(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        } else {
            BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

            if (user.isNew()) {
                Number newKey = insertUser.executeAndReturnKey(parameterSource);
                user.setId(newKey.intValue());
            } else {
                if (namedParameterJdbcTemplate.update("""
                           UPDATE users SET name=:name, email=:email, password=:password, 
                           registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                        """, parameterSource) == 0) {
                    return null;
                }
                jdbcTemplate.update("DELETE FROM user_role WHERE user_id=?", user.id());
            }
            insertRoles(user);
            return user;
        }
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_role ur on users.id = ur.user_id WHERE id=?", RSE, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        Collection<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_role ur on users.id = ur.user_id WHERE email=?", RSE, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_role ur on users.id = ur.user_id ORDER BY name, email", RSE);
    }

    private void insertRoles(User user) {
        Set<Role> roles = user.getRoles();
        jdbcTemplate.batchUpdate("INSERT INTO user_role (user_id, role) VALUES (?, ?)", roles, roles.size(),
                (ps, role) -> {
                    ps.setInt(1, user.id());
                    ps.setString(2, role.name());
                });
    }
}
