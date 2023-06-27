import cv2

# Read the image
img = cv2.imread('2.png')

# Convert to greyscale
grey = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# Find the laplacian of this image and
# calculate the variance
var = cv2.Laplacian(grey, cv2.CV_64F).var()

print(var)

# if variance is less than the set threshold
# image is blurred otherwise not
if var < 120:
    print('Image is Blurred')
else:
    print('Image Not Blurred')
