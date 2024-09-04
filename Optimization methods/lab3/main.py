import os
import psutil
import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt
from sklearn.linear_model import Lasso
from sklearn.linear_model import Ridge
from sklearn.linear_model import ElasticNet
from sklearn.linear_model import LinearRegression
from sklearn.preprocessing import PolynomialFeatures


# Main.Task №1 and №2
def stochastic_gradient_descent(
        x, y,
        start,
        batch=32,
        epochs=50,
        learning_rate_method='standard'
):
    learning_rate = {
        'standard': lambda *args: 1e-2,
        'step_decay': lambda k: 0.1 * (0.9 ** (k // 100)),
        'linear_decay': lambda k: max(1e-3, 1e-1 - k * 1e-3)
    }

    it = 0
    w, b = start

    for epoch in range(epochs):
        for i in range(0, x.shape[0], batch):
            it += 1

            xi = x[i:i + batch]
            yi = y[i:i + batch]

            h = learning_rate[learning_rate_method](it)

            predict = np.dot(xi, w) + b

            w = w - h * (2 / batch) * np.dot(xi.T, predict - yi)
            b = b - h * (2 / batch) * np.sum(predict - yi)

    return {'w': w, 'b': b, 'it': it}


# Main.Task №3
def tensorflow_optimizer(
        X, Y,
        batch=32,
        epochs=50,
        optimizer='SGD'
):
    optimizers = {
        'SGD': tf.optimizers.legacy.SGD(learning_rate=0.01),
        'Momentum': tf.optimizers.legacy.SGD(learning_rate=0.01, momentum=0.9),
        'Nesterov': tf.optimizers.legacy.SGD(learning_rate=0.01, momentum=0.9, nesterov=True),
        'AdaGrad': tf.optimizers.legacy.Adagrad(learning_rate=2.0),
        'RMSProp': tf.optimizers.legacy.RMSprop(learning_rate=0.01),
        'Adam': tf.optimizers.legacy.Adam(learning_rate=0.01)
    }

    X = tf.constant(X, dtype=tf.float32)
    Y = tf.constant(Y, dtype=tf.float32)

    w = tf.Variable(tf.random.normal([1, 1]), name='weight')
    b = tf.Variable(tf.random.normal([1]), name='bias')

    def model(x):
        return tf.matmul(x, w) + b

    def loss_fun(y_true, y_pred):
        return tf.reduce_mean(tf.square(y_true - y_pred))

    for epoch in range(epochs):
        for i in range(0, X.shape[0], batch):
            xi = X[i:i + batch]
            yi = Y[i:i + batch]
            with tf.GradientTape() as tape:
                y_pred = model(xi)
                loss = loss_fun(yi, y_pred)
            gradients = tape.gradient(loss, [w, b])
            optimizers[optimizer].apply_gradients(zip(gradients, [w, b]))

    return w.numpy(), b.numpy()


# Additional.Task №1
def polynomial_regression(x, y, degree=2, bounds=(-1, 1), method='standard'):
    methods = {
        'standard': LinearRegression(),
        'L1': Lasso(alpha=0.1),
        'L2': Ridge(alpha=0.1),
        'Elastic': ElasticNet(alpha=0.1)
    }

    features = PolynomialFeatures(degree=degree, include_bias=True)
    x_poly = features.fit_transform(x)

    reg = methods[method]
    reg.fit(x_poly, y)

    X = np.linspace(*bounds, x.shape[0]).reshape(x.shape[0], 1)
    X_poly = features.transform(X)

    Y = reg.predict(X_poly)

    return X, Y


def create_2d_linear_plot(x, y, w, b):
    fig, ax = plt.subplots()
    for point in zip(x, y):
        ax.scatter(*point)
    ax.plot(x, w * x + b)


def create_3d_linear_plot(x, y, z, w, b):
    fig = plt.figure()
    ax = fig.add_subplot(projection='3d')

    for point in zip(x, y, z):
        ax.scatter(*point, s=0.1)

    x_grid, y_grid = np.meshgrid(np.linspace(x.min(), x.max(), 100),
                                 np.linspace(y.min(), y.max(), 100))

    z_grid = w[0] * x_grid + w[1] * y_grid + b

    ax.plot_wireframe(x_grid, y_grid, z_grid,
                      alpha=0.5, rstride=20, cstride=20, color='b')


def create_2d_polynomial_plot(x, y, X, Y):
    for point in zip(x, y):
        plt.scatter(*point)
    plt.plot(X, Y, c='r', linewidth=2)


def main():
    """
    :MAIN:
    Stochastic gradient descent:
    np.random.seed(234)
    X = 2 * np.random.rand(200, 1)
    Y = 6 * X + np.random.randn(200, 1) + 12

    SGD and Modifications:
    np.random.seed(543)
    X = -1.43 * 1.45 * np.random.rand(10000, 1)
    Y = 7 * X + 2 * np.random.rand(10000, 1)
    Z = 2 * X + 5 * Y + 10 * np.random.rand(10000, 1) + 3

    :EXTRA:
    Polynomial regression:
    np.random.seed(412)
    X = 10 * np.random.rand(50, 1) - 5
    Y1 = 1 - (X ** 2 / 2) + (X ** 4 / 24) - (X ** 6 / 720) + (X ** 8 / 40320) + 5 * np.random.rand(50, 1)
    Y2 = X - (X ** 3 / 6) + (X ** 5 / 120) - (X ** 7 / 5040) + (X ** 9 / 362880) + 5 * np.random.rand(50, 1)
    """

    np.random.seed(234)
    X = 2 * np.random.rand(200, 1)
    Y = 6 * X + np.random.randn(200, 1) + 12

    start = (np.array([[0]]), np.array([0]))
    result = stochastic_gradient_descent(
        X, Y, start, epochs=20, batch=32, learning_rate_method='step_decay')

    print(result)

    create_2d_linear_plot(X, Y, result['w'], result['b'])

    plt.show()


if __name__ == '__main__':
    process = psutil.Process(os.getpid())
    main()
