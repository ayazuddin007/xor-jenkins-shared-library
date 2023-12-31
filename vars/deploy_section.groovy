import com.xor.monorepo.section3
import com.xor.features.ansibleRepo
import com.xor.features.gchatNotification
	
def call(String clusterName, String slaveName) {
	
	def section3 = new section3(this, env, params, scm, currentBuild)
	def ansibleRepo = new ansibleRepo(this, env, params, scm, currentBuild)
	def gchatNotification = new gchatNotification(this, env, params, scm, currentBuild)
	
	pipeline {
	
		    parameters {
			string(name: 'controlRepos', description: '' )
			string(name: 'cr_groupId', description: '')
			string(name: 'artifactId', description: '')
			string(name: 'version', description: '')
			string(name: 'registryId', description: '' )
			string(name: 'section1_build_number', description: '')
			string(name: 'section2_build_number', description:'' )
			string(name: 'namespaces', description:'' )
			string(name: 'dockerImage', description: '')
			string(name: 'credentialsId', description: '')
			string(name: 'orgName', description: '')    
		    }
  
    		    agent { label "${slaveName}"}
   
		    environment {
			PATH = "/opt/maven/bin:$PATH"
		    } 
    
    		    stages {
		    	
			stage('Create Yaml'){
				steps {
					script {
						section3.createYamlFile(clusterName)
				     	}
				}
			}
			    
			stage('Ansible Checkout'){
				steps {
					script {
						ansibleRepo.ansibleRepoCheckout()
				     	}
				}
			}   
			    
			stage('Deploy to cluster'){
				steps {
					script {
						section3.deployToCluster()
						ansibleRepo.ansibleRepoRemove()
				     	}
				}
				post  { 
					success {
						script {	
							gchatNotification.success()
						}
					}
					failure {
						script {	
							gchatNotification.failure()
						}
					}
					cleanup { 
						script { 
							cleanWs()
						}
					}
				}
			}    
			
      
}
}
}  
