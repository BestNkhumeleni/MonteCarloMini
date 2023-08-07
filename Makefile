#Variables
SRC_DIR := src
OUT_DIR := bin
#Rules
default:
	javac -d bin src/MonteCarloMini/*.java
clean:
	rm $(OUT_DIR)/*.class
	rm $(SRC_DIR)/*.class
	rm -rf $(OUT_DIR)