/* groovylint-disable CompileStatic, DuplicateStringLiteral, GStringExpressionWithinString, LineLength */
// Jenkinsfile先于Docker 并执行Dockerfile配置
pipeline {
    // agent 指定在哪个节点上运行 Pipeline 或者阶段
    agent any
    // environment 用于在 pipeline 或者阶段级别定义环境变量
    environment {
        NETWORK = 'ruoyi'                          // 前后端互通网络组
        IMAGE_NAME = 'colaclub-admin'              // 定义 Docker 镜像的名字 JAR存放目录名字
        JAR_FILE = 'colaclub-admin.jar'            // 定义 JAR 文件名，可根据实际情况调整
        WS = "${WORKSPACE}"                        // 定义工作空间路径
        PROFILE = 'prod'                           // 读取yml配置  	
    }

    // stages 包含所有执行阶段
    stages {
        // 第一个阶段: 环境检查
        stage('1.Enviroment') {
            // steps 包含需要执行的步骤
            steps {
                sh 'pwd && ls -alh'  // 显示当前路径和列表
                sh 'printenv'        // 打印环境变量
                sh 'docker version'  // 显示 Docker 版本
                sh 'java -version'   // 显示 Java 版本
                sh 'git --version'   // 显示 Git 版本
            }
        }

        // 第二个阶段: 代码编译
        stage('2.Compile') {
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
                sh 'cd ${WS} && mvn clean package -Dmaven.test.skip=true' // 执行 Maven 编译和打包，跳过测试
            }
        }

        // 第三个阶段: Docker 镜像打包
        stage('3.Build') {
            steps {
                sh 'pwd && ls -alh'
                sh 'echo ${WS}'                                      // 打印工作空间路径
                sh 'ls -alh ${WS}/'                                  // 列出 ${WS} 目录的所有文件和文件夹
                sh 'ls -lah ${WS}/${IMAGE_NAME}/target/'             // 列出构建JAR是否存在
                // 使用 Docker 构建镜像
                sh 'docker build --build-arg PROFILE=${PROFILE} -t ${IMAGE_NAME} -f Dockerfile ${WS}/${IMAGE_NAME}/target/'
                // 存在同名容器删除 如果不存在则忽略错误
                sh 'docker rm -f ${IMAGE_NAME} || true'
            }
        }

        stage('4.Deploy') {
            steps {
                // 这是后端容器运行的端口 前端nginx需配置后端监听的端口
                // 即application-prod.yml中的port
                sh 'docker run -d --net ${NETWORK} -p 8887:80 --name ${IMAGE_NAME} ${IMAGE_NAME}'
            }
        }
    }
}
