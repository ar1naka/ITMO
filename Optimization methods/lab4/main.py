import optuna
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import Normalize
from sympy import Symbol, lambdify, parsing


# Main.Task № 1 and 2
def artificial_bee_colony(
        f,
        low,
        high,
        num_bees=20,
        num_elite=5,
        iterations=500,
        num_best_sites=10,
        neighborhood_size=0.1,
        callback=None
):
    it = 0

    fitness = [None]
    bees = np.random.uniform(low, high, (num_bees, 2))

    if callback:
        callback(bees)

    for _ in range(iterations):
        it += 1

        fitness = np.array([f(bee[0], bee[1]) for bee in bees])

        sorted_indices = np.argsort(fitness)
        bees = bees[sorted_indices]
        fitness = fitness[sorted_indices]

        for i in range(num_elite, num_bees):
            bees[i] = np.random.uniform(low, high, 2)

        for i in range(num_elite):
            for j in range(num_best_sites):
                new_bee = bees[i] + np.random.uniform(
                    -neighborhood_size, neighborhood_size, 2)
                new_fitness = f(new_bee[0], new_bee[1])
                if new_fitness < fitness[i]:
                    bees[i] = new_bee
                    fitness[i] = new_fitness

        if callback:
            callback(bees)

    best_solution = bees[0]
    best_fitness = fitness[0]

    return {'x': best_solution, 'fun': best_fitness, 'it': it}


# Additional.Task №1
def optuna_minimize(f, callback):
    def objective(trial):
        x = trial.suggest_float('x', -5, 5)
        y = trial.suggest_float('y', -5, 5)

        return f(x, y)

    study = optuna.create_study(direction='minimize')
    study.optimize(objective, n_trials=250, callbacks=[callback])

    return {'x': study.best_params, 'y': study.best_value}


# Additional.Task №2
np.random.seed(234)
X = 2 * np.random.rand(200, 1)
Y = 6 * X + np.random.randn(200, 1) + 12

start = (np.zeros((1,)), np.zeros((1,)))


def loss_function(w, b, X, Y):
    pred = np.dot(X, w) + b
    return np.mean((pred - Y) ** 2)


def stochastic_gradient_descent(
        x, y,
        start,
        batch=2,
        eps=1e-5,
        epochs=50,
        learning_rate_method='standard'
):
    it = 0
    w, b = start

    learning_rate = {
        'standard': lambda *args: 1e-2,
        'step_func': lambda it: max(1e-3, 0.1 - it * 1e-3)
    }

    for epoch in range(epochs):
        for i in range(0, x.shape[0], batch):
            it += 1

            xi = x[i:i + batch]
            yi = y[i:i + batch]

            h = learning_rate[learning_rate_method](it)

            predict = np.dot(xi, w) + b

            w_k = w - h * (2 / batch) * np.dot(xi.T, predict - yi)
            b_k = b - h * (2 / batch) * np.sum(predict - yi)

            if (np.linalg.norm(w_k - w) < eps
                    or np.linalg.norm(b_k - b) < eps):
                return {'w': w, 'b': b, 'it': it}

            w, b = w_k, b_k

    return {'w': w, 'b': b, 'it': it}


def newton_method(f, x, grad, hess, eps=1e-7,
                  method='standard', callback=None):
    methods = {
        'standard': lambda *args: 1,
    }

    it = 0

    while True:
        it += 1

        if callback:
            callback(x)

        gradient = grad(x)
        hessian_inv = np.linalg.inv(hess(x))

        delta = hessian_inv.dot(np.transpose(gradient))

        x_prev = x
        x = x - methods[method](f, x, delta, grad) * delta

        if (np.linalg.norm(delta) < eps or
                np.linalg.norm(x - x_prev) < eps):
            return {'x': x, 'fun': f(x), 'it': it}


def objective_sgd(trial):
    eps = trial.suggest_float('eps', 1e-8, 1e-1, log=True)
    learning_rate = trial.suggest_float('learning_rate', 1e-5, 1e-1, log=True)

    result = stochastic_gradient_descent(
        X, Y,
        start,
        eps=eps,
        learning_rate_method='standard'
    )

    loss = loss_function(result['w'], result['b'], X, Y)
    return loss


def objective_newton(trial):
    eps = trial.suggest_float('eps', 1e-8, 1e-1, log=True)

    def f(x):
        pred = np.dot(X, x[0]) + x[1]
        return np.mean((pred - Y) ** 2)

    def grad(x):
        pred = np.dot(X, x[0]) + x[1]
        error = pred - Y
        grad_w = 2 * np.mean(X * error)
        grad_b = 2 * np.mean(error)
        return np.array([grad_w, grad_b])

    def hess(x):
        hess_w = 2 * np.mean(X ** 2)
        hess_b = 2
        return np.array([[hess_w, 0], [0, hess_b]])

    result = newton_method(f, np.array([start[0][0], start[1][0]]),
                           grad, hess, eps=eps, method='standard')

    return result['fun']


def study_method(method):
    study = optuna.create_study(direction='minimize')
    study.optimize(method, n_trials=100)


def level_lines(x, surface_func, bounds=(-10, 10), num=100, levels=10):
    fig = plt.figure()
    axes = fig.add_subplot(111)
    axes.set_xlabel('X')
    axes.set_ylabel('Y')
    axes.set_title('Level lines')

    xgrid, ygrid = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                               np.linspace(bounds[0], bounds[1], num))

    norm = Normalize(vmin=0, vmax=len(x))
    for i in range(len(x)):
        color = plt.cm.cool(norm(i))
        plt.scatter(*x[i], s=10, color=color)

    axes.contour(xgrid, ygrid, surface_func([xgrid, ygrid]), levels=levels)


def create_surface(x_val, fun_val, surface_func, bounds=(-10, 10), num=100):
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    X, Y = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                       np.linspace(bounds[0], bounds[1], num))
    Z = surface_func(np.array([X, Y]))

    ax.plot_surface(X, Y, Z, cmap='BuPu')
    colors = np.linspace(0, 1, len(x_val))

    for i in range(len(x_val)):
        ax.plot(*x_val[i], fun_val[i], '.',
                label='top', zorder=4, markersize=5, c=plt.cm.cool(colors[i]))


def main():
    """
    :MAIN:
    Artificial bee colony:
    Himmelblau function:
    f = (x^2 + y - 11)^2 + (x + y^2 - 7)^2
    Isome function:
    f = -cos(x) * cos(y) * exp(-((x - pi)^2 + (y - pi)^2))
    Rosenbrock function:
    f = (1 - x)^2 + 100 * (y - x^2)^2
    """

    x, y = Symbol('x'), Symbol('y')
    # parse = '(3 - x)**2 + 20 * (y - x**2)**2'
    parse = '(1 - x)**2 + 100 * (y - x**2)**2'
    # parse = '(x**2 + y - 11)**2 + (x + y**2 - 7)**2'
    # parse = '(x + 2 * y - 7)**2 + (2 * x + y - 5)**2'
    # parse = 'sin(x + y) + (x - y)**2 - 1.5 * x + 2.5 * y + 1'
    # parse = '-cos(x) * cos(y) * exp(-((x - pi)**2 + (y - pi)**2))'

    foo = parsing.parse_expr(parse)

    f = lambdify((x, y), foo)

    x_val, fun_val = [], []

    def bees_callback(bees):
        for bee in bees:
            x_val.append(bee)
            fun_val.append(f(*bee))

    result = artificial_bee_colony(f, -5.12, 5.12, callback=None)

    print(result)

    def optuna_callback(hist, _):
        x_val.append(list(hist.best_params.values()))
        fun_val.append(hist.best_value)

    result = optuna_minimize(f, optuna_callback)

    print(result)

    if x_val and fun_val:
        level_lines(x_val, lambda vec: f(*vec), bounds=(-5, 5), levels=10)

        create_surface(x_val, fun_val, lambda vec: f(*vec), bounds=(-5, 5))

        plt.show()


if __name__ == '__main__':
    main()

