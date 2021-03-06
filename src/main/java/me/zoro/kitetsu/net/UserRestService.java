package me.zoro.kitetsu.net;

import me.zoro.kitetsu.model.ApiResponseDTO;
import me.zoro.kitetsu.model.IDEntity;
import me.zoro.kitetsu.model.IDSEntity;
import me.zoro.kitetsu.model.UserDO;
import me.zoro.kitetsu.mysql.mybatis.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luguanquan
 * @date 2020-03-14 19:41
 * <p>
 * 在这个示例中将通过和数据库结合，把网络请求到数据库更新结合起来。示例更加完善
 * 只是示例，对于各种异常并没有做校验
 * </p>
 */
@RestController
public class UserRestService {

	@Autowired
	private UserMapper userMapper;

	@PostMapping("kitetsu/user/create")
	public ResponseEntity<ApiResponseDTO<UserDO>> createUser(@RequestBody UserDO userDO) {
		userMapper.insert(userDO);
		ApiResponseDTO<UserDO> resp = new ApiResponseDTO<>(userDO);
		return ResponseEntity.ok(resp);
	}

	/**
	 * @param idEntity TODO 注意这个参数不需要 @RequestParam,什么时候才用 ？
	 * @return
	 */
	@GetMapping("kitetsu/user/get")
	public ResponseEntity<ApiResponseDTO<UserDO>> queryUser(IDEntity idEntity) {
		UserDO userDO = userMapper.findById(idEntity.getId());
		ApiResponseDTO<UserDO> resp = new ApiResponseDTO<>(userDO);
		if (userDO == null) {
			resp.setCode(1);
			resp.setMessage("id = " + idEntity.getId() + " 的用户不存在");
		}
		return ResponseEntity.ok(resp);
	}

	/**
	 * 提供 MergeRequestRestService.java 做请求合并示例
	 *
	 * @param idsEntity
	 * @return
	 */
	@GetMapping
	public ResponseEntity<List<ApiResponseDTO<UserDO>>> queryUsersBatch(IDSEntity idsEntity) {
		List<ApiResponseDTO<UserDO>> list = new ArrayList<>();
		// 这里还可以再优化查询，但这里先简单使用，主要是构建批量查询方式
		for (Long id : idsEntity.getIds()) {
			// 访问数据库方式
//			UserDO userDO = userMapper.findById(id);
			//临时简单返回方式
			UserDO userDO = new UserDO();
			userDO.setId(id);
			userDO.setName("zoro" + id);

			ApiResponseDTO<UserDO> resp = new ApiResponseDTO<>(userDO);
			if (userDO == null) {
				resp.setCode(1);
				resp.setMessage("id = " + id + " 的用户不存在");
			}
			list.add(resp);
		}
		return ResponseEntity.ok(list);
	}

	@PostMapping("kitetsu/user/delete")
	public ResponseEntity<ApiResponseDTO> deleteUser(@RequestBody IDEntity idEntity) {
		Integer success = userMapper.deleteById(idEntity.getId());
		ApiResponseDTO resp = new ApiResponseDTO();
		if (success != null && success > 0) {
			resp.setCode(0);
			resp.setMessage("success");
		} else {
			// 这个一般是抛出全局的错误，捕获后再返回，但现在临时快速处理验证
			resp.setCode(2);
			resp.setMessage("找不到用户");
		}
		return ResponseEntity.ok(resp);
	}

}
