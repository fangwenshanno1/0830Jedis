package com.atguigu.jedis.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

public class TestJedis {

	// redis-cli -h xxx -p xxx
	// 报错： can not connect to server
	// 检查服务端配置文件bind绑定的地址，不能绑定127.0.0.1,检查防火墙是否已经关闭
	@SuppressWarnings("resource")
	@Test
	public void testPing() {

		// 创建一个客户端对象
		Jedis jedis = new Jedis("192.168.6.3", 6379, 6000);

		// 使用客户端对象，发送命令，调用对应的方法
		String pong = jedis.ping();

		System.out.println(pong);

		// 使用完后及时关闭客户端
		jedis.close();

	}

	@Test
	public void testPool() {
		// 默认的连接池配置
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

		System.out.println(poolConfig);

		JedisPool jedisPool = new JedisPool(poolConfig, "192.168.6.3", 6379, 60000);

		// 从池中获取连接
		Jedis jedis = jedisPool.getResource();

		String ping = jedis.ping();

		System.out.println(ping);

		// 如果是从连接池中获取的，那么执行close方法只是将连接放回到池中
		jedis.close();

		jedisPool.close();
	}

	@Test
	public void testSentinel() throws Exception {
		Set<String> set = new HashSet<>();
		// set中放的是哨兵的Ip和端口
		set.add("192.168.6.3:26379");
		
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		
		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", set, poolConfig, 60000);
		
		Jedis jedis = jedisSentinelPool.getResource();
		
		String value = jedis.get("s3");
		
		jedis.set("Jedis", "Jedis1");
		
		System.out.println(value);
		
		System.out.println(value);
	}
	
	@Test
	public void testCluster(){
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		
		// 放入集群中任意一个节点即可
		jedisClusterNodes.add(new HostAndPort("192.168.6.3", 6379));
		jedisClusterNodes.add(new HostAndPort("192.168.6.3", 6380));
		
		JedisCluster jc = new JedisCluster(jedisClusterNodes);
		
		jc.set("foo", "bar");
		
		String value = jc.get("foo");
		
		System.out.println(value);
	}


}
