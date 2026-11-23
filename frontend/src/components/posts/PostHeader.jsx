import Avatar from '../common/Avatar';
import PostMenu from './PostMenu';

import { getInitials } from '../../utils/userUtils';

import {
    formatPostDate,
    hasBeenEdited,
} from '../../utils/dateUtils';

function PostHeader({
                        post,
                        isPostAuthor,
                        showPostMenu,
                        onToggleMenu,
                        onEdit,
                        onDelete,
                        postLoading,
                    }) {
    return (
        <div className="post-header">
            <Avatar
                initials={getInitials(post.authorUsername)}
                color="lavender"
            />

            <div className="post-author">
                <strong>{post.authorUsername}</strong>

                <span className="post-date">
                    {formatPostDate(post.createdAt)}

                    {hasBeenEdited(
                        post.createdAt,
                        post.updatedAt
                    ) && (
                        <span className="edited-indicator">
                            · Edited
                        </span>
                    )}
                </span>
            </div>

            {isPostAuthor && (
                <PostMenu
                    show={showPostMenu}
                    onToggle={onToggleMenu}
                    onEdit={onEdit}
                    onDelete={onDelete}
                    loading={postLoading}
                />
            )}
        </div>
    );
}

export default PostHeader;