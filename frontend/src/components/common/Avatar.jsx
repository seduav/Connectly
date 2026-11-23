function Avatar({
                    initials,
                    color = 'lavender',
                    size = 'normal',
                }) {
    return (
        <div className={`avatar avatar-${color} avatar-${size}`}>
            {initials}
        </div>
    );
}

export default Avatar;