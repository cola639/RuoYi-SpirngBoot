pipeline{
    // agent 指定在哪个节点上运行 Pipeline 或者阶段
    agent any
    // environment 用于在 pipeline 或者阶段级别定义环境变量
    environment {
      IMAGE_NAME = "ruoyi-admin"  // 定义 Docker 镜像的名字
      WS = "${WORKSPACE}"         //定义工作空间路径
    }

    // stages 包含所有执行阶段
    stages {
        // 第一个阶段: 环境检查
        stage('1.Enviroment'){
            // steps 包含需要执行的步骤
            steps {
               sh 'pwd && ls -alh' // 显示当前路径和列表
               sh 'printenv'       // 打印环境变量
               sh 'docker version' // 显示 Docker 版本
               sh 'java -version'  // 显示 Java 版本
               sh 'git --version'  // 显示 Git 版本
            }
        }

        // 第二个阶段: 代码编译
        stage('2.Compile'){
            // agent 可以被用于改变某个阶段的执行位置
            agent {
                docker {
                    image 'maven:3-alpine' // 使用 Maven Docker 镜像
                    args '-v maven-repository:/root/.m2' // 设置参数
                 }
            }
            steps {
               sh 'pwd && ls -alh'
               sh 'mvn -v' // 显示 Maven 版本
               // 执行 Maven 编译和打包，跳过测试
               sh 'cd ${WS} && mvn clean package -Dmaven.test.skip=true'
            }
        }

        // 第三个阶段: Docker 镜像打包
        stage('3.Build'){
            steps {
               sh 'pwd && ls -alh'
               sh 'echo ${WS}' //打印工作空间路径
               // 将nginx.conf 打包进来
               sh 'cp ${WS}/ruoyi_nginx.conf ${WS}/${IMAGE_NAME}/target/'
               // 使用 Docker 构建镜像
               sh 'docker build -t ${IMAGE_NAME} -f Dockerfile ${WS}/${IMAGE_NAME}/target/'
               sh 'ls'
            }
        }

        // 第四个阶段: 部署服务
        stage('4.Deploy'){
            // 删除可能存在的同名容器和虚悬镜像 -f强制停止并删除
            steps {
               sh 'pwd && ls -alh'
               sh 'docker rm -f ${IMAGE_NAME} || true && docker rmi $(docker images -q -f dangling=true) || true'
               sh 'docker start ruoyi-mysql && docker start ruoyi-redis'
               // 运行 Docker 镜像，链接到 MySQL 和 Redis 服务，并挂载宿主机日志目录
               sh 'docker run -d -p 8888:8080 --name ${IMAGE_NAME} --link ruoyi-mysql:mysql --link ruoyi-redis:redis -v /mydata/logs/${IMAGE_NAME}:/logs/${IMAGE_NAME} ${IMAGE_NAME}'
               sh 'ls'
            }
        }
    }
}
