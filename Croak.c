#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

int main(int argc, char **argv)
{
	int ret;

	chdir("/home/mendax/Executables/Croak2");
	ret = execl("/usr/local/java/bin/java", "/usr/local/java/bin/java",  
		"-classpath", 
		"lib/Images.jar:lib/jdom2.0.5.jar:lib/mysql-connector-java-5.1.34-bin",
		"-jar", "Croak2.jar", NULL);
	if (ret == -1)
	{
		printf("Could not run Croak: %s\n", strerror(errno));
		return -1;
	}

	return 0;
}

